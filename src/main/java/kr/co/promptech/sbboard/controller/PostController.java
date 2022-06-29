package kr.co.promptech.sbboard.controller;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.File;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.dto.FileDto;
import kr.co.promptech.sbboard.model.dto.PostThumbDto;
import kr.co.promptech.sbboard.model.helper.CurrentUser;
import kr.co.promptech.sbboard.model.vo.PostVo;
import kr.co.promptech.sbboard.service.FileService;
import kr.co.promptech.sbboard.service.PostService;
import kr.co.promptech.sbboard.service.ThumbService;
import kr.co.promptech.sbboard.util.ResultHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileService fileService;
    private final ThumbService thumbService;

    private final ModelMapper modelMapper;

    @Value("${upload.relative-path}")
    private String FILE_PATH;

    @GetMapping("")
    public String newPost(Model model) {
        model.addAttribute("postVo", new PostVo());

        return "app/post/new";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, @CurrentUser Account account, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "redirect:/";
        }
        model.addAttribute(post);

        post = postService.increaseViews(post);

        PostThumbDto postThumbDto = thumbService.getThumbDtoByPost(post, account);
        model.addAttribute(postThumbDto);

        return "app/post/show";
    }

    @PostMapping(value = "")
    public String create(@CurrentUser Account account,
                         @ModelAttribute("postVo") @Valid PostVo postVo,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "app/post/new";
        }

        Post post = postService.save(postVo, account);

        try {
            fileService.saveAll(postVo.getFiles(), post);
        } catch (Exception e) {
            return "app/post/new";
        }

        return "redirect:/";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, @CurrentUser Account account, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "/errors/component/404";
        }
        if (!post.isWriter(account)) {
            return "redirect:/";
        }

        PostVo postVo = modelMapper.map(post, PostVo.class);
        model.addAttribute(postVo);

        return "app/post/edit";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        postService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<?> files(@PathVariable("id") Long id) {
        Post post = postService.findById(id);
        List<File> fileList = new ArrayList<>(post.getFileSet());
        List<FileDto> fileDtoList = modelMapper.map(fileList,
                new TypeToken<List<FileDto>>() {
                }.getType());

        return ResponseEntity.ok().body(fileDtoList);
    }

    @PostMapping("/{id}/file")
    public ResponseEntity<?> uploadFile(@PathVariable("id") Long id,
                                        @RequestPart(value = "file") MultipartFile multipartFile) {

        Post post = postService.findById(id);
        UUID uuid = UUID.randomUUID();

        File file;
        try {
            file = fileService.generateFileInfo(multipartFile, post.getId(), uuid.toString());
        } catch (Exception e) {
            log.info(e.getMessage());
            ResultHandler resultHandler = ResultHandler.builder()
                    .isSuccess(false)
                    .errorMessage("파일 업로드 중 에러가 발생했습니다. 다시 시도해주세요.").build();
            return ResponseEntity.badRequest().body(resultHandler);
        }
        fileService.uploadFile(multipartFile, file);

        post.addFile(file);
        postService.save(post);

        HashMap<String, String> result = new HashMap<>();

        String url = String.format("%s%s/%s", FILE_PATH, post.getId(), file.getName());
        result.put("url", url);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/{id}")
    public String update( @ModelAttribute("postVo") @Valid PostVo postVo,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "app/post/edit";
        }

        Post post = postService.update(postVo);
        try {
            fileService.saveAll(postVo.getFiles(), post);
        } catch (Exception e) {
            return "app/post/edit";
        }

        return "redirect:/posts/" + post.getId();
    }

    @ModelAttribute
    private void setActive(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
    }
}
