package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.File;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.dto.PostDto;
import kr.co.promptech.sbboard.model.enums.BoardType;
import kr.co.promptech.sbboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.Destination;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(null);
    }

    public void save(PostDto postDto, Account account) {

        Post post = modelMapper.map(postDto, Post.class);
        post.setBoardType(BoardType.FREE);

        post.setAccount(account);
        log.info("======================");
        postRepository.save(post);

        File file = postDto.getFile();
        post.setFile(file);
        file.setPost(post);
        postRepository.save(post);

    }

    public List<Post> findAll() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
