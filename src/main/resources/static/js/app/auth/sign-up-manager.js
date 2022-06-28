const SignUpManager = function(form) {
    this.form = form

    this.initEvent();
}
SignUpManager.prototype.initEvent = function () {
    Checker.initPasswordCheckTrueEvent()
    this.addCheckValidityEvent();
}

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