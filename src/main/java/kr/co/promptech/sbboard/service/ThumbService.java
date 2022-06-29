package kr.co.promptech.sbboard.service;

import kr.co.promptech.sbboard.model.Account;
import kr.co.promptech.sbboard.model.Post;
import kr.co.promptech.sbboard.model.Thumb;
import kr.co.promptech.sbboard.model.dto.PostThumbDto;
import kr.co.promptech.sbboard.repository.ThumbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThumbService {

    private final ThumbRepository thumbRepository;

    public void thumbsUp(Account account, Post post) {

        Thumb thumb = Thumb.builder().post(post).account(account).build();

        thumbRepository.save(thumb);
    }

    public PostThumbDto getThumbDtoByPost(Post post, Account account) {
        int postThumbCount = this.countThumbsByPost(post);
        boolean userClicked = this.checkUserClicked(post, account);

        return PostThumbDto.builder()
                .postThumbCount(postThumbCount)
                .userClicked(userClicked).build();
    }

    public int countThumbsByPost(Post post) {
        return thumbRepository.countAllByPost(post);
    }

    public boolean checkUserClicked(Post post, Account account) {

        return thumbRepository.existsByPostAndAccount(post, account);
    }

}
