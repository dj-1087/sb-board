const ThumbManager = function () {
    this.token = document.querySelector('meta[name="_csrf"]').content;
    this.header = document.querySelector('meta[name="_csrf_header"]').content;

    this.postId = document.getElementById("post-id").value;
    this.thumbsUpButton = document.getElementById("thumbs-up-btn");
    this.thumbsCount = document.getElementById("thumbs-count").innerText;
    this.userClicked = document.getElementById("user-clicked").value;

    this.initEvent();
    console.log('user clicked(type boolean): ', this.userClicked === true)
    console.log('user clicked(type string): ', this.userClicked === 'true')
    console.log('thumbs count: ', this.thumbsCount)
};

ThumbManager.prototype.initEvent = function () {
    const self = this;
    this.thumbsUpButton.addEventListener("click", function () {
        self.thumbsUp().then(() => self.refreshView());
    });
};

ThumbManager.prototype.thumbsUp = async function () {
    const self = this
    if (self.userClicked === 'true') {
        alert("이미 해당 게시물에 대해 공감을 눌렀습니다.")
        return false;
    }
    const response = await fetch("/thumbs/" + self.postId, {
        method: "GET",
        mode: "cors",
        headers: {
            [self.header]: self.token,
        }
    });
    console.log("======thumbs up result======")
    if (response.status === 400) {
        console.log("======thumbs up error======")
        const error = await response.json();
        console.log(error)
        alert(error.errorMessage);
        return false;
    }
    const postThumbDto = await response.json();
    self.thumbsCount = postThumbDto.postThumbCount;
};

ThumbManager.prototype.refreshView = function() {
    const self = this;

    const thumbsCountSpan = document.getElementById("thumbs-count");
    thumbsCountSpan.innerText = self.thumbsCount;
    const userClickedInput = document.getElementById("user-clicked");
    userClickedInput.innerText = self.userClicked;
}