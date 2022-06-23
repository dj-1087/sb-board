const SignUpManager = function(form) {
    this.form = form

    this.initEvent();
}
SignUpManager.prototype.initEvent = function () {
    this.addPasswordCheckTrueEvent();
    this.addCheckValidityEvent();
}

SignUpManager.prototype.addPasswordCheckTrueEvent = function () {
    const self = this;

    const passwordCheckInput = document.getElementById("password-check");
    const helpSpan = document.getElementById("password-check-help");
    const errorSpan = document.getElementById("password-check-error")

    passwordCheckInput.addEventListener("change", function () {
        const passwordValue = document.getElementById("password").value;
        if (passwordValue === this.value) {
            errorSpan.classList.add('d-none')
            helpSpan.classList.remove('d-none')
        } else {
            errorSpan.classList.remove('d-none');
            helpSpan.classList.add('d-none')
        }
    });
};

SignUpManager.prototype.addCheckValidityEvent = function () {
    const self = this;
    if (!self.form) {
        return false
    }
    self.form.addEventListener('submit', function(event) {
        if (!this.checkValidity() || !self.passwordCheckTrue()) {
            event.preventDefault();
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    }, false)
}

SignUpManager.prototype.passwordCheckTrue = function () {
    const passwordValue = document.getElementById("password").value;
    const passwordCheckValue = document.getElementById("password-check").value;

    return passwordValue === passwordCheckValue;


};