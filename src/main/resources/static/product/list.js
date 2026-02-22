class ProductListManager {
    constructor() {
        this.currentPage = 0;
        this.isLast = false;
        this.isLoading = false;

        this.container = document.getElementById('product-grid');
        this.loader = document.getElementById('loader');
        this.sentinel = document.getElementById('sentinel');
        this.errorBanner = document.getElementById('error-banner');

        this.init();
    }

    init() {
        this.bindEvents();
        this.fetchProducts();
    }

    bindEvents() {
        const observerOptions = {
            root: null,
            rootMargin: '100px',
            threshold: 0
        };

        this.observer = new IntersectionObserver((entries) => {
            if (entries[0].isIntersecting && !this.isLoading && !this.isLast) {
                console.log("📍 바닥 감지됨! 다음 페이지 호출");
                this.fetchProducts();
            }
        }, observerOptions);

        this.observer.observe(this.sentinel);
    }

    async fetchProducts() {
        if (this.isLoading || this.isLast) return;

        this.setLoading(true);
        this.hideError();
        console.log(`📡 요청 중... 페이지: ${this.currentPage}`);

        try {
            const response = await fetch(`/api/products?page=${this.currentPage}&size=3&sort=id,desc`);

            if (!response.ok) {
                throw new Error(`서버 오류: ${response.status}`);
            }

            const result = await response.json();

            if (result.status === "success" && result.data) {
                const { content, last } = result.data;

                if (content && content.length > 0) {
                    this.render(content);
                    this.isLast = last;
                    this.currentPage++;

                    // sentinel이 아직 화면에 보이면 다음 페이지 자동 호출
                    // setTimeout 대신 requestAnimationFrame으로 렌더링 완료 후 확인
                    requestAnimationFrame(() => {
                        requestAnimationFrame(() => {
                            const rect = this.sentinel.getBoundingClientRect();
                            if (rect.top < window.innerHeight && !this.isLast && !this.isLoading) {
                                console.log("💡 데이터가 적어 자동으로 다음 페이지 호출");
                                this.fetchProducts();
                            }
                        });
                    });
                } else {
                    this.isLast = true;
                }
            }
        } catch (e) {
            console.error(e);
            this.showError();
            // 에러 시 isLast를 true로 막지 않음 → 재시도 가능
        } finally {
            this.setLoading(false);
        }
    }

    render(products) {
        products.forEach(p => {
            const card = this.createCardElement(p);
            this.container.appendChild(card);
        });
    }

    // XSS 방지: innerHTML/insertAdjacentHTML 대신 DOM API로 직접 생성
    createCardElement(product) {
        const formattedPrice = new Intl.NumberFormat('ko-KR').format(product.currentPrice);

        const card = document.createElement('div');
        card.className = 'product-card mb-3';
        // card.style.cursor = 'pointer';
        card.addEventListener('click', () => {
            window.location.href = `/products/${product.id}`;
        });

        // 카드 왼쪽 바 색상
        if (product.diffPrice > 0) {
            card.classList.add('card-up');
        } else if (product.diffPrice < 0) {
            card.classList.add('card-down');
        }

        // 상품명
        const infoDiv = document.createElement('div');
        infoDiv.className = 'product-info';

        const nameDiv = document.createElement('div');
        nameDiv.className = 'product-name';
        nameDiv.textContent = product.name;
        infoDiv.appendChild(nameDiv);

        // 가격 + 등락 + 변동일
        const priceSection = document.createElement('div');
        priceSection.className = 'price-section';

        // 현재 가격
        const priceDiv = document.createElement('div');
        priceDiv.className = 'current-price';
        priceDiv.textContent = formattedPrice;

        const priceUnit = document.createElement('span');
        priceUnit.className = 'price-unit';
        priceUnit.textContent = '원';
        priceDiv.appendChild(priceUnit);
        priceSection.appendChild(priceDiv);

        // 등락 정보
        const diffDiv = document.createElement('div');

        if (product.diffPrice !== null && product.diffPrice !== 0) {
            diffDiv.className = 'price-diff ' + (product.diffPrice > 0 ? 'diff-up' : 'diff-down');

            const arrow = product.diffPrice > 0 ? '▲' : '▼';
            const absDiff = Math.abs(product.diffPrice).toLocaleString('ko-KR');
            const absRate = Math.abs(product.diffRate).toFixed(1);
            diffDiv.textContent = `${arrow} ${absDiff}원 (${absRate}%)`;
            priceSection.appendChild(diffDiv);

            // 변동일
            const changedAtDiv = document.createElement('div');
            changedAtDiv.className = 'price-changed-at';
            changedAtDiv.textContent = this.formatChangedAt(product.priceChangedAt);
            priceSection.appendChild(changedAtDiv);
        } else {
            diffDiv.className = 'price-changed-at';
            diffDiv.textContent = '-';
            priceSection.appendChild(diffDiv);
        }

        card.appendChild(infoDiv);
        card.appendChild(priceSection);

        return card;
    }

    setLoading(isLoading) {
        this.isLoading = isLoading;
        if (this.loader) this.loader.style.display = isLoading ? 'block' : 'none';
    }

    showError() {
        if (this.errorBanner) {
            this.errorBanner.style.display = 'block';
        }
    }

    hideError() {
        if (this.errorBanner) {
            this.errorBanner.style.display = 'none';
        }
    }

    formatChangedAt(isoString) {
        if (!isoString) return '';

        const changed = new Date(isoString);
        const now = new Date();
        const diffDays = Math.floor((now - changed) / (1000 * 60 * 60 * 24));

        if (diffDays === 0) return '오늘';
        if (diffDays === 1) return '어제';
        if (diffDays < 7) return `${diffDays}일 전`;
        if (diffDays < 30) return `${Math.floor(diffDays / 7)}주 전`;
        return `${Math.floor(diffDays / 30)}개월 전`;
    }
}

document.addEventListener('DOMContentLoaded', () => new ProductListManager());
