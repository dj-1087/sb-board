class Checker{
    static initPasswordCheckTrueEvent() {
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
    }
}