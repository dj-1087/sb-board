const CommentManager = function () {
    this.token = document.querySelector('meta[name="_csrf"]').content;
    this.header = document.querySelector('meta[name="_csrf_header"]').content;

    this.commentSection = document.getElementById("comment-section");
    this.saveButton = document.querySelector("#comment-form .save-btn")

    this.commentTemplate = Handlebars.getTemplate("comment/comment-index");

    this.postId = document.getElementById("post-id").value;
    this.commentList = []

    this.initEvent();
}
CommentManager.prototype.initEvent = function () {
    console.log("=======init=======")
    const self = this;

    this.saveButton.onclick = function (event) {
        event.preventDefault();
        self.saveComment()
    }
}

CommentManager.prototype.saveComment = async function () {

    const self = this;
    console.log("self 값")
    console.log(self)

    console.log("token 값")
    console.log(self.token)

    const form = document.getElementById("comment-form");
    const data = self.getFormObject(form);
    data["postId"] = self.postId;

    console.log("data 값")
    console.log(JSON.stringify(data))

    const response = await fetch("/comments", {
        method: "POST",
        mode: 'cors',
        headers: {
            'X-CSRF-TOKEN': self.token,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    });

    const comment = await response.json()

    console.log("response data")
    console.log(comment)

    const datetime = comment.createdAt
    comment.createdAt = self.formatToDate(datetime);

    self.commentList.push(comment)

    self.refreshView()
}

CommentManager.prototype.refreshView = function () {
    console.log("=========comment list=========")
    console.log(this.commentList)
    const self = this

    self.commentSection.innerHTML = "";
    for (const comment of self.commentList) {
        const commentView = self.commentTemplate({
            id: comment.id,
            nickname: comment.nickname,
            content: comment.content,
            createdAt: comment.createdAt
        })
        self.commentSection.innerHTML += commentView;
    }
};

CommentManager.prototype.getFormObject = function (formElement) {
    const formData = new FormData(formElement);

    const formJson = {}
    formData.forEach(((value, key) => formJson[key] = value));

    return formJson
}

CommentManager.prototype.formatToDate = function (datetime) {
    console.log("datetime")
    console.log(datetime)
    moment.locale('ko');
    return moment(datetime).format("LL");
};