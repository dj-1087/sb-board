class ResetPasswordManager {
    constructor() {
        this.resetPasswordForm = undefined;
        this.sendMailForm = undefined;

        this.loadingHandler = new LoadingHandler();
        this.loadingHandler.initLoadingView();

        this.initEventListener();
    }

    initEventListener() {
        console.log("init event listener")
        const self = this;
        // self.loadingHandler = new LoadingHandler();

        self.resetPasswordForm = document.getElementById('reset-password-form')
        if (self.resetPasswordForm) {
            self.resetPasswordForm.addEventListener('submit', function (event) {
                console.log("resetPasswordForm")
                self.checkFormValidity(event, this)
            }, false);
            Checker.initPasswordCheckTrueEvent()
        }

        self.sendMailForm = document.getElementById("send-mail-form")
        console.log(self.sendMailForm)
        self.sendMailForm?.addEventListener('submit', function (event) {
            console.log("sendMailForm")
            self.loadingHandler.start()
            self.checkFormValidity(event, this);
        }, false)


    }

    checkFormValidity(event, form) {
        console.log('do submit event')
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
            this.loadingHandler.end();
        }
        form.classList.add('was-validated');
    }
}