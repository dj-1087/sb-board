package kr.co.promptech.sbboard.model;

import kr.co.promptech.sbboard.model.base.BaseTimeEntity;
import kr.co.promptech.sbboard.model.enums.BoardType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    List<Comment> comments = new ArrayList<>();

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> fileSet = new HashSet<>();

    public void addFile(File file) {
        file.setPost(this);
        this.fileSet.add(file);
    }
}
