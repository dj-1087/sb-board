package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.vo.PostVo;
import kr.co.promptech.sbboard.model.enums.BoardType;
import kr.co.promptech.sbboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(null);
    }

    public Post save(PostVo postVo, Account account) {

        Post post = modelMapper.map(postVo, Post.class);
        post.setBoardType(BoardType.FREE);

        post.setAccount(account);
        log.info("======================");
        return postRepository.save(post);

//        File file = postDto.getFile();
//        post.setFile(file);
//        file.setPost(post);
//        postRepository.save(post);

    }

    public List<Post> findAll() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
