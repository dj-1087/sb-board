const SignUpManager = function(form) {
    this.form = form

    this.initEvent();
}
SignUpManager.prototype.initEvent = function () {
    this.addCheckValidityEvent();
}

SignUpManager.prototype.addCheckValidityEvent = function () {
    console.log("add validity")
    if (!this.form) {
        return false
    }
    this.form.addEventListener('submit', function(event) {
        console.log('do submit event')
        if (!this.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }
        this.classList.add('was-validated')
    }, false)
    console.log("added")
}