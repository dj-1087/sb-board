package kr.co.promptech.sbboard.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @DisplayName("addFile - 정상")
    @Test
    void addFile() {

        File file = File.builder()
                .name("name")
                .originalName("originalName")
                .path("/path")
                .ext(".ext").build();

        Post post = Post.builder().title("title").build();

        post.addFile(file);
        assertThat(post.getFileSet().size()).isEqualTo(1);
        assertThat(post.getFileSet().contains(file)).isTrue();
    }
}