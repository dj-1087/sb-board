class ResetPasswordManager {
    constructor() {
        // this.resetPasswordForm = document.getElementById("reset-password-form")
        // this.sendMailForm = document.getElementById("send-mail-form")
        this.resetPasswordForm = undefined;
        this.sendMailForm = undefined;

        this.loadingHandler = new LoadingHandler();
        this.loadingHandler.initLoadingView();

        // this.loadingHandler = undefined;

        this.initEventListener();
    }

    initEventListener() {
        console.log("init event listener")
        const self = this;
        // self.loadingHandler = new LoadingHandler();

        // console.log(self.sendMailForm)
        self.resetPasswordForm = document.getElementById('reset-password-form')
        self.resetPasswordForm?.addEventListener('submit', function (event) {
            console.log("resetPasswordForm")
            self.checkFormValidity(event, this)
        }, false);

        self.sendMailForm = document.getElementById("send-mail-form")
        self.sendMailForm?.addEventListener('submit', function (event) {
            console.log("sendMailForm")
            self.loadingHandler.start()
            self.checkFormValidity(event, this);
        }, false)

        // self.resetPasswordForm = document.getElementById('reset-password-form')
        // self.resetPasswordForm?.addEventListener('submit', function(event) {
        //     console.log('do submit event')
        //     if (!this.checkValidity()) {
        //         event.preventDefault();
        //         event.stopPropagation();
        //     }
        //     this.classList.add('was-validated');
        // }, false)

        // self.sendMailForm = document.getElementById("send-mail-form")
        // self.sendMailForm?.addEventListener('submit', function(event) {
        //     self.loadingHandler.start();
        //     console.log('do submit event')
        //     if (!this.checkValidity()) {
        //         event.preventDefault();
        //         event.stopPropagation();
        //         self.loadingHandler.end();
        //     }
        //     this.classList.add('was-validated');
        // }, false)
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