const CommentManager = function () {
    this.token = document.querySelector('meta[name="_csrf"]').content;
    this.header = document.querySelector('meta[name="_csrf_header"]').content;

    this.commentSection = document.getElementById("comment-section");
    this.saveButton = document.querySelector("#comment-form .save-btn")

    this.commentShowTemplate = Handlebars.getTemplate("comment/show");
    this.commentEditTemplate = Handlebars.getTemplate("comment/edit");

    this.postId = document.getElementById("post-id").value;
    this.commentList = []

    this.user = {};

    this.initEvent();
    this.loadData().then(() => this.refreshView());
}
CommentManager.prototype.initEvent = function () {
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
    const form = document.getElementById("comment-form");
    const data = self.getFormObject(form);
    data["postId"] = self.postId;

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

    const datetime = comment.createdAt
    comment.createdAt = self.formatToDate(datetime);

    self.commentList.push(comment)
}

CommentManager.prototype.refreshView = function () {
    const self = this

    self.commentSection.innerHTML = "";
    for (const comment of self.commentList) {
        const commentView = self.commentShowTemplate({
            id: comment.id,
            nickname: comment.accountNickname,
            content: comment.content,
            createdAt: comment.createdAt,
            isAdmin: self.user.admin,
            isWriter: comment.accountId === self.user.id
        })
        self.commentSection.innerHTML += commentView;
    }

    document.querySelector('.comments-count').innerHTML = self.commentList.length + '';

    document.querySelectorAll('.remove-comment-btn').forEach((removeButton) => {
        removeButton.addEventListener('click', async function (event) {
            event.preventDefault();
            if (!confirm("?????? ????????? ?????????????????????????")) {
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
};

CommentManager.prototype.loadUser = async function () {
    const self = this;
    const response = await fetch("/auth/session", {method:'GET', mode:'cors'});
    self.user = await response.json();
};

CommentManager.prototype.loadData = function () {
    const self = this;

    return Promise.all([self.loadCommentList(), self.loadUser()]);
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
    const data = _.find(self.commentList, {'id': parseInt(id)})

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