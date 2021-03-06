package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.enums.BoardType;
import kr.co.promptech.sbboard.model.vo.PostVo;
import kr.co.promptech.sbboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    public Post findById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post save(PostVo postVo, Account account) {

        Post post = modelMapper.map(postVo, Post.class);

        post.setAccount(account);
        return postRepository.save(post);
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public Post update(PostVo postVo) {

        Post post = postRepository.findById(postVo.getId()).orElse(null);
        if (post == null) {
            return null;
        }
        post.setTitle(postVo.getTitle());
        post.setContent(postVo.getContent());
        post.setBoardType(postVo.getBoardType());
        return postRepository.save(post);
    }

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> findAllByBoardType(BoardType boardType, Pageable pageable) {
        return postRepository.findAllByBoardType(boardType, pageable);
    }

    public Page<Post> search(String keyword, Pageable pageable) {
        return postRepository.findAllByTitleContaining(keyword, pageable);
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    public Post increaseViews(Post post) {
        int views = post.getViews() + 1;
        post.setViews(views);
        return postRepository.save(post);
    }
}
