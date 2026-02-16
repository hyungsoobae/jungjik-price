class ProductListManager {
    constructor() {
        this.currentPage = 0;
        this.isLast = false;
        this.isLoading = false;

        this.container = document.getElementById('product-grid');
        this.loader = document.getElementById('loader');
        this.sentinel = document.getElementById('sentinel');

        this.init();
    }

    init() {
        this.bindEvents();
        this.fetchProducts(); // 첫 2개 로드
    }

    bindEvents() {
        const observerOptions = {
            root: null,
            rootMargin: '100px', // 바닥 100px 근처면 미리 호출
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
        console.log(`📡 요청 중... 페이지: ${this.currentPage}`);

        try {
            const response = await fetch(`/api/products?page=${this.currentPage}&size=3&sort=id,desc`);
            const result = await response.json();

            if (result.status === "success" && result.data) {
                const { content, last } = result.data;

                if (content && content.length > 0) {
                    this.render(content);
                    this.isLast = last;
                    this.currentPage++;

                    // [핵심 추가] 카드를 그렸는데도 아직 sentinel이 화면에 보인다면 (데이터가 너무 적으면)
                    // 다음 페이지를 한 번 더 자동으로 부릅니다.
                    setTimeout(() => {
                        const rect = this.sentinel.getBoundingClientRect();
                        if (rect.top < window.innerHeight && !this.isLast) {
                            console.log("💡 데이터가 적어 자동으로 다음 페이지 호출");
                            this.fetchProducts();
                        }
                    }, 100);

                } else {
                    this.isLast = true;
                }
            }
        } catch (e) {
            console.error(e);
            this.isLast = true;
        } finally {
            this.setLoading(false);
        }
    }

    render(products) {
        const html = products.map(p => this.createCardHtml(p)).join('');
        this.container.insertAdjacentHTML('beforeend', html);
    }

    createCardHtml(product) {
        const formattedPrice = new Intl.NumberFormat('ko-KR').format(product.currentPrice);
        return `
        <div class="product-card shadow-sm mb-3">
            <div class="product-info">
                <div class="product-name" style="font-weight: 600; font-size: 1.1rem; line-height: 1.4; word-break: keep-all;">
                    ${product.name}
                </div>
            </div>

            <div class="price-section">
                <div class="current-price">
                    ${formattedPrice}<span class="price-unit">원</span>
                </div>
                <a href="/products/${product.id}" class="btn btn-dark btn-sm px-3" style="border-radius: 6px; font-weight: 500;">
                    상세보기
                </a>
            </div>
        </div>
    `;
    }

    setLoading(isLoading) {
        this.isLoading = isLoading;
        if (this.loader) this.loader.style.display = isLoading ? 'block' : 'none';
    }
}

document.addEventListener('DOMContentLoaded', () => new ProductListManager());