package kr.co.promptech.sbboard.model;

import kr.co.promptech.sbboard.model.base.BaseTimeEntity;
import kr.co.promptech.sbboard.model.enums.BoardType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
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

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    List<Thumb> thumbs = new ArrayList<>();

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> fileSet = new HashSet<>();

    public void addFile(File file) {
        if (this.getFileSet() == null) {
            this.setFileSet(new HashSet<>());
        }
        file.setPost(this);
        this.fileSet.add(file);
    }
}
