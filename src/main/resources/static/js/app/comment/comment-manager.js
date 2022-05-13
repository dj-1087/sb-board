const CommentManager = function () {
    this.token = document.querySelector('meta[name="_csrf"]').content;
    this.header = document.querySelector('meta[name="_csrf_header"]').content;

    this.commentSection = document.getElementById("comment-section");
    this.saveButton = document.querySelector("#comment-form .save-btn")

    this.commentShowTemplate = Handlebars.getTemplate("comment/show");
    this.commentEditTemplate = Handlebars.getTemplate("comment/edit");

    this.postId = document.getElementById("post-id").value;
    this.commentList = []

    this.initEvent();
    this.loadCommentList().then(() => this.refreshView());
}
CommentManager.prototype.initEvent = function () {
    console.log("=======init=======")
    const self = this;

    self.saveButton.onclick = function (event) {
        event.preventDefault();
        self.saveComment().then(() => self.refreshView())
        self.resetForm();
    }

}

CommentManager.prototype.resetForm = function () {
    const textArea = document.getElementById('comment');
    textArea.value = "";
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
            [self.header]: self.token,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    });

    const comment = await response.json();

    console.log("response data")
    console.log(comment)

    const datetime = comment.createdAt
    comment.createdAt = self.formatToDate(datetime);

    self.commentList.push(comment)
}

CommentManager.prototype.refreshView = function () {
    console.log("=========comment list=========")
    console.log(this.commentList)
    const self = this

    self.commentSection.innerHTML = "";
    for (const comment of self.commentList) {
        const commentView = self.commentShowTemplate({
            id: comment.id,
            nickname: comment.accountNickname,
            content: comment.content,
            createdAt: comment.createdAt
        })
        self.commentSection.innerHTML += commentView;
    }

    document.querySelectorAll('.remove-comment-btn').forEach((removeButton) => {
        removeButton.addEventListener('click', async function (event) {
            event.preventDefault();
            if (!confirm("해당 댓글을 삭제하시겠습니까?")) {
                return false
            }

            await self.removeComment(this.dataset.id);
            await self.loadCommentList();
            await self.refreshView();
        })
    });

    document.querySelectorAll('.edit-comment-btn').forEach((editButton) => {
        editButton.addEventListener('click', function (event) {
            event.preventDefault();

            self.changeToEditView(this.dataset.id);
        })
    });
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

CommentManager.prototype.loadCommentList = async function () {
    const self = this;

    const response = await fetch("/comments?postId=" + this.postId, { method: "GET", mode: 'cors' })
        .catch((error) => {
            alert(error)
            return false
        });
    const dataList =  await response.json();

    self.commentList = _.map(dataList, (comment) => {
        comment.createdAt = self.formatToDate(comment.createdAt);
        return comment
    });
    console.log("data loaded")
    console.log(self.commentList)

};

CommentManager.prototype.removeComment = function (id) {
    const self = this;

    return fetch("/comments/" + id, {
        method: "DELETE",
        mode: 'cors',
        headers: {
            [self.header]: self.token,
        },
    })
};
CommentManager.prototype.editComment = function (id) {
    const self = this;
    const form = document.querySelector(`#comment-${id} form`)
    const data = self.getFormObject(form);

    return fetch("/comments/" + id, {
        method: "PUT",
        mode: 'cors',
        headers: {
            [self.header]: self.token,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
};

CommentManager.prototype.changeToEditView = function (id) {
    const self = this

    const view = document.querySelector(`#comment-${id} .card-body`)
    const data = _.find(self.commentList, {'id': id})
    console.log("edit data")
    console.log('id> ', id)
    console.log(data);

    view.innerHTML = self.commentEditTemplate({
        id: data.id,
        content: data.content,
        createdAt: data.createdAt
    })

    const editButton = document.querySelector(`#comment-${id} .edit-btn`)
    editButton.addEventListener('click', async function () {
        await self.editComment(id);
        await self.loadCommentList();
        await self.refreshView();
    })
}