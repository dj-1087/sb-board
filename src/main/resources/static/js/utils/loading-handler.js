class LoadingHandler {
    constructor() {
        this.defaultLoadingView = Handlebars.getTemplate("loading");
        this.loadingOverlay = undefined;
    }

    initLoadingView() {
        const mainContentDiv = document.querySelector(".main-content");
        mainContentDiv.innerHTML += this.defaultLoadingView()

        this.loadingOverlay = document.querySelector('.loading-overlay')
    }

    start() {
        this.loadingOverlay.classList.add('is-active')
    }

    end() {
        this.loadingOverlay.classList.remove('is-active')
    }
    
    /*로딩 버튼 안에 로딩전에 표시할 element와 로딩 중에 표시될 element가 있어야 함
    - .before-loading: 로딩전에 표시할 element
    - .loading: 로딩전에 표시할 element*/
    startButtonLoading(targetButton) {
        const beforeLoadingElement = targetButton.querySelector('.before-loading');
        const loadingElement = targetButton.querySelector('.loading');
        beforeLoadingElement.classList.add('visually-hidden')
        loadingElement.classList.remove('visually-hidden')
    }

    /*로딩 버튼 안에 로딩전에 표시된 element와 로딩 중에 표시한 element가 있어야 함
    - .before-loading: 로딩전에 표시된 element
    - .loading: 로딩전에 표시한 element*/
    endButtonLoading(targetButton) {
        const beforeLoadingElement = targetButton.querySelector('.before-loading');
        const loadingElement = targetButton.querySelector('.loading');
        beforeLoadingElement.classList.remove('visually-hidden')
        loadingElement.classList.add('visually-hidden')
    }
}