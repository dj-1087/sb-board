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

    this.loadFileList().then(() => this.refreshFileView());
};

PostManager.prototype.initEvent = function () {
    const self = this;

    self.fileInput.addEventListener('change', function () {
        console.log("changed")
        for (const file of self.fileInput.files) {
            self.fileList.items.add(file)
        }
        console.log(self.fileList)
        self.refreshFileView();
        console.log("refreshFileView")
    });

    self.form.onsubmit = function (event) {
        event.preventDefault();
        self.fileInput.files = self.fileList.files;
        self.form.submit();
    }

    // self.removePostButton.addEventListener("click", function (event) {
    //     event.preventDefault();
    //     console.log("remove ")
    //     self.removePost(this.dataset.id)
    // });
}

PostManager.prototype.resetFileInput = function () {
    this.fileInput.files = new DataTransfer().files
}

PostManager.prototype.refreshFileView = function () {
    const self = this;
    console.log(self)

    self.fileInfoSection.innerHTML = "";
    console.log(self.fileList.files)

    for (const fileInfo of self.fileInfoList) {
        const fileInfoView = self.fileShowTemplate({
            id: fileInfo.id,
            fileName: fileInfo.originalName,
            isNew: false
        })

        self.fileInfoSection.innerHTML += fileInfoView;
    }

    for (let i = 0; i < self.fileList.files.length; i++) {
        console.log("idx", i)
        const fileInfo = self.fileList.files[i];
        console.log(fileInfo.name)

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
            if (!confirm("해당 첨부파일을 삭제하시겠습니까?")) {
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
    console.log("fileInfo")
    console.log(fileInfo)
    const blob = new Blob([fileInfo], {type: fileInfo.type});
    console.log(blob)
    return URL.createObjectURL(blob)
};

PostManager.prototype.loadFileList = async function () {
    console.log("loading file list")
    const self = this;
    console.log("postId: ",self.postId)

    if (!self.postId) {
        return false
    }

    const response = await fetch("/posts/"+self.postId+"/files", { method: "GET", mode: 'cors' })
        .catch((error) => {
            alert(error)
            return false
        });

    console.log(response)
    self.fileInfoList = await response.json();

    console.log("data loaded")
    console.log(self.fileInfoList)
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

    return fetch("/posts/" + id, {
        method: "DELETE",
        mode: 'cors',
        headers: {
            [self.header]: self.token,
        },
    })
};