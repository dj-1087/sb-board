package kr.co.promptech.sbboard.model;

import kr.co.promptech.sbboard.model.base.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String name;
    private String originalName;
    private String path;
    private String ext;

}
