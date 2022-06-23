const PostManager = function () {
    this.token = document.querySelector('meta[name="_csrf"]').content;
    this.header = document.querySelector('meta[name="_csrf_header"]').content;

    this.postId = document.getElementById("post-id")?.value;
    this.removePostButton = document.querySelector('.remove-post-btn')

    this.form = document.getElementById("post-form");
    this.fileInfoSection = document.getElementById("file-info-section");
    this.fileInput = document.getElementById("files");

    this.fileShowTemplate = Handlebars.getTemplate("file/show");

    this.fileList = new DataTransfer();
    this.fileInfoList = [];


    this.initEvent();
    if (this.form) {
        this.loadFileList().then(() => this.refreshFileView());
    }
};

PostManager.prototype.initEvent = function () {
    const self = this;

    if (self.form) {
        self.fileInput.addEventListener('change', function () {
            for (const file of self.fileInput.files) {
                self.fileList.items.add(file)
            }
            self.refreshFileView();
        });

        self.form.onsubmit = function (event) {
            event.preventDefault();
            self.fileInput.files = self.fileList.files;
            self.form.submit();
        }

        $('#summernote').summernote({
            placeholder: '게시물 내용을 입력해주세요.',
            tabsize: 2,
            lang: "ko-KR",
            height: 300,
            callbacks: {	//여기 부분이 이미지를 첨부하는 부분
                onImageUpload: function (files, editor, welEditable) {
                    // uploadSummernoteImageFile(files[0], this);
                    for (let i = 0; i < files.length; i++) {
                        self.uploadSummernoteImage(files[i], this);
                    }
                },
                onPaste: function (e) {
                    const clipboardData = e.originalEvent.clipboardData;
                    if (clipboardData && clipboardData.items && clipboardData.items.length) {
                        const item = clipboardData.items[0];
                        if (item.kind === 'file' && item.type.indexOf('image/') !== -1) {
                            e.preventDefault();
                        }
                    }
                }
            }
        });
    }

    if (self.removePostButton) {
        self.removePostButton.addEventListener("click", function (event) {
            event.preventDefault();
            self.removePost(this.dataset.id).then(() => {
                location.href = "/"
            }).catch((error) => {
                console.log("error occurred")
                console.log(error)
            })
        });
    }
}

PostManager.prototype.resetFileInput = function () {
    this.fileInput.files = new DataTransfer().files
}

PostManager.prototype.refreshFileView = function () {
    const self = this;

    self.fileInfoSection.innerHTML = "";

    for (const fileInfo of self.fileInfoList) {
        const fileInfoView = self.fileShowTemplate({
            id: fileInfo.id,
            fileName: fileInfo.originalName,
            isNew: false
        })

        self.fileInfoSection.innerHTML += fileInfoView;
    }

    for (let i = 0; i < self.fileList.files.length; i++) {
        const fileInfo = self.fileList.files[i];

        const fileInfoView = self.fileShowTemplate({
            seq: i,
            fileName: fileInfo.name,
            isNew: true
        })
        self.fileInfoSection.innerHTML += fileInfoView;
    }

    document.querySelectorAll('.remove-file-btn').forEach((removeButton) => {
        removeButton.addEventListener('click', function (event) {
            event.preventDefault()
            if (!confirm("해당 첨부파일을 영구적으로 삭제하시겠습니까?")) {
                return false
            }

            if (this.dataset.isnew === "false") {
                self.removeFile(this.dataset.id).then(() => self.refreshFileView());
            } else {
                self.fileList.items.remove(this.dataset.seq);
                self.refreshFileView();
            }
        })
    });

    document.querySelectorAll('.download-btn').forEach((downloadButton) => {
        if (downloadButton.dataset.isnew === "false") {
            return false;
        }

        const href = self.getDownloadHref(downloadButton.dataset.seq);
        downloadButton.setAttribute('href', href);
    })

    self.resetFileInput();
};

PostManager.prototype.getDownloadHref = function (seq) {
    const self = this;

    const fileInfo = self.fileList.files[seq];
    const blob = new Blob([fileInfo], {type: fileInfo.type});
    return URL.createObjectURL(blob)
};

PostManager.prototype.loadFileList = async function () {
    const self = this;

    if (!self.postId) {
        return false
    }

    const response = await fetch("/posts/"+self.postId+"/files", { method: "GET", mode: 'cors' })
        .catch((error) => {
            alert(error)
            return false
        });

    self.fileInfoList = await response.json();
};

PostManager.prototype.removeFile = async function (id) {
    const self = this;

    await fetch("/files/" + id, {
        method: "DELETE",
        mode: 'cors',
        headers: {
            [self.header]: self.token,
        },
    })

    _.remove(self.fileInfoList, {id});
};

PostManager.prototype.removePost = async function (id) {
    const self = this;

    return fetch(`/posts/${id}`, {
        method: "DELETE",
        mode: 'cors',
        headers: {
            [self.header]: self.token,
        },
    })
};

PostManager.prototype.uploadSummernoteImage = async function (file, el) {

    // TODO 추후 manager 객체로 변환시 수정 필요
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    // const postId = document.getElementById('post-id').value;
    const form_data = new FormData();
    form_data.append('file', file);

    const response = await fetch(`/files/summernote`, {
        method: "POST",
        mode: 'cors',
        headers: {
            [header]: token,
            // 'Content-Type': 'multipart/form-data',
        },
        body: form_data,
    });

    if (response.ok) {
        const data = await response.json();
        $(el).summernote('insertImage', data.url);
    } else {
        alert(`${file.name} 파일 업로드를 실패했습니다.`)
        return false;
    }
}