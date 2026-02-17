/**
 * detail.js - 상품 상세 페이지
 *
 * ── 사용 API ─────────────────────────────────────────────────
 *  1) 상품 기본 정보 : GET /api/products/{id}
 *     응답: ApiResponse<Product>
 *
 *  2) 가격 이력 (전체)   : GET /api/products/{id}/price-histories
 *  3) 가격 이력 (기간별) : GET /api/products/{id}/price-histories?days=7|14|30
 *     응답: ApiResponse<List<PriceHistoryResponse>>
 *     PriceHistoryResponse: { id, price, collectedAt }
 * ────────────────────────────────────────────────────────────
 */

class ProductDetailPage {

    constructor(productId) {
        this.productId = productId;
        this.allHistories = [];
        this.chart = null;

        this.$overlay  = document.getElementById("loading-overlay");
        this.$error    = document.getElementById("error-state");
        this.$content  = document.getElementById("main-content");
    }

    async init() {
        try {
            const [product, histories] = await Promise.all([
                this.fetchProduct(),
                this.fetchHistories()
            ]);

            this.allHistories = histories;
            this.renderProductInfo(product);
            this.renderStats(histories);
            this.renderChart(histories);
            this.bindPeriodTabs();

            this.showContent();
        } catch (e) {
            console.error("상세 페이지 초기화 실패:", e);
            this.showError();
        }
    }

    // ── 데이터 패칭 ─────────────────────────────────────────

    async fetchProduct() {
        const res = await fetch(`/api/products/${this.productId}`);
        if (!res.ok) throw new Error(`상품 조회 실패: ${res.status}`);
        const json = await res.json();
        return json.data;
    }

    async fetchHistories(days = null) {
        const url = days
            ? `/api/products/${this.productId}/price-histories?days=${days}`
            : `/api/products/${this.productId}/price-histories`;

        const res = await fetch(url);
        if (!res.ok) throw new Error(`히스토리 조회 실패: ${res.status}`);
        const json = await res.json();
        return json.data; // [{ id, price, collectedAt }, ...]
    }

    // ── 렌더링 ──────────────────────────────────────────────

    renderProductInfo(product) {
        const fmt = (n) => n.toLocaleString("ko-KR") + "원";

        document.getElementById("display-source").textContent      = product.source;
        document.getElementById("display-name").textContent        = product.name;
        document.getElementById("display-current-price").textContent = fmt(product.currentPrice);
        document.getElementById("header-source-badge").textContent = product.source;

        const urlEl = document.getElementById("display-url");
        urlEl.href = product.productUrl;
    }

    renderStats(histories) {
        if (!histories.length) return;

        const prices = histories.map(h => h.price);
        const min = Math.min(...prices);
        const max = Math.max(...prices);
        const avg = Math.round(prices.reduce((a, b) => a + b, 0) / prices.length);
        const fmt = (n) => n.toLocaleString("ko-KR") + "원";

        document.getElementById("stat-min").textContent = fmt(min);
        document.getElementById("stat-avg").textContent = fmt(avg);
        document.getElementById("stat-max").textContent = fmt(max);
    }

    renderChart(histories) {
        const fmt = (n) => n.toLocaleString("ko-KR") + "원";

        const series = histories.map(h => ({
            x: new Date(h.collectedAt).getTime(),
            y: h.price
        }));

        const minPrice = Math.min(...histories.map(h => h.price));
        const maxPrice = Math.max(...histories.map(h => h.price));
        const padding  = Math.round((maxPrice - minPrice) * 0.15) || 5000;

        // 최초 1회만 인스턴스 생성
        if (!this.chart) {
            const options = {
                series: [{ name: "가격", data: series }],
                chart: {
                    type: "area",
                    height: 320,
                    toolbar: { show: false },
                    zoom:    { enabled: false },
                    animations: {
                        enabled: true,
                        easing: "easeinout",
                        speed: 600
                    },
                    fontFamily: "'Pretendard', 'Apple SD Gothic Neo', sans-serif"
                },
                dataLabels: { enabled: false },
                stroke: {
                    curve: "smooth",
                    width: 2.5,
                    colors: ["#0d6efd"]
                },
                fill: {
                    type: "gradient",
                    gradient: {
                        shadeIntensity: 1,
                        opacityFrom: 0.25,
                        opacityTo:   0.02,
                        stops: [0, 95, 100],
                        colorStops: [{
                            offset: 0,
                            color: "#0d6efd",
                            opacity: 0.25
                        }, {
                            offset: 100,
                            color: "#0d6efd",
                            opacity: 0.02
                        }]
                    }
                },
                xaxis: {
                    type: "datetime",
                    labels: {
                        style: { fontSize: "12px", colors: "#868e96" },
                        datetimeFormatter: {
                            year: "yyyy",
                            month: "MM/dd",
                            day:   "MM/dd",
                            hour:  "MM/dd HH:mm"
                        }
                    },
                    axisBorder: { show: false },
                    axisTicks:  { show: false }
                },
                yaxis: {
                    min: minPrice - padding,
                    max: maxPrice + padding,
                    labels: {
                        style: { fontSize: "12px", colors: "#868e96" },
                        formatter: (val) => val.toLocaleString("ko-KR") + "원"
                    }
                },
                grid: {
                    borderColor: "#f1f3f5",
                    strokeDashArray: 4,
                    xaxis: { lines: { show: false } }
                },
                tooltip: {
                    theme: "dark",
                    x: { format: "yyyy.MM.dd HH:mm" },
                    y: { formatter: (val) => fmt(val) }
                },
                markers: {
                    size: 0,
                    hover: { size: 5, sizeOffset: 2 }
                },
                annotations: {
                    points: this.buildAnnotations(histories)
                }
            };

            this.chart = new ApexCharts(document.getElementById("price-chart"), options);
            this.chart.render();
            return;
        }

        // 기간 필터 전환 시 — DOM 조작 없이 데이터와 y축 범위만 교체
        this.chart.updateOptions({
            yaxis: {
                min: minPrice - padding,
                max: maxPrice + padding,
                labels: {
                    style: { fontSize: "12px", colors: "#868e96" },
                    formatter: (val) => val.toLocaleString("ko-KR") + "원"
                }
            },
            annotations: {
                points: this.buildAnnotations(histories)
            }
        }, false, false); // (옵션교체, redraw전체X, animate유지)

        this.chart.updateSeries([{ name: "가격", data: series }]);
    }

    buildAnnotations(histories) {
        if (!histories.length) return [];

        const prices   = histories.map(h => h.price);
        const minPrice = Math.min(...prices);
        const maxPrice = Math.max(...prices);

        const minItem = histories.find(h => h.price === minPrice);
        const maxItem = histories.find(h => h.price === maxPrice);

        const fmt = (n) => n.toLocaleString("ko-KR") + "원";
        const annotations = [];

        if (minItem) {
            annotations.push({
                x: new Date(minItem.collectedAt).getTime(),
                y: minItem.price,
                marker: { size: 7, fillColor: "#1a9e5f", strokeColor: "#fff", strokeWidth: 2, radius: 3 },
                label: {
                    text: "최저 " + fmt(minItem.price),
                    style: { background: "#1a9e5f", color: "#fff", fontSize: "11px", fontWeight: 600, padding: { top: 4, bottom: 4, left: 8, right: 8 } },
                    borderRadius: 6,
                    offsetY: -6
                }
            });
        }

        if (maxItem && maxPrice !== minPrice) {
            annotations.push({
                x: new Date(maxItem.collectedAt).getTime(),
                y: maxItem.price,
                marker: { size: 7, fillColor: "#d9363e", strokeColor: "#fff", strokeWidth: 2, radius: 3 },
                label: {
                    text: "최고 " + fmt(maxItem.price),
                    style: { background: "#d9363e", color: "#fff", fontSize: "11px", fontWeight: 600, padding: { top: 4, bottom: 4, left: 8, right: 8 } },
                    borderRadius: 6,
                    offsetY: -6
                }
            });
        }

        return annotations;
    }

    // ── 기간 필터 ────────────────────────────────────────────

    bindPeriodTabs() {
        document.getElementById("period-tabs").addEventListener("click", async (e) => {
            const tab = e.target.closest(".period-tab");
            if (!tab) return;

            document.querySelectorAll(".period-tab").forEach(t => t.classList.remove("active"));
            tab.classList.add("active");

            const period = tab.dataset.period;
            const days   = period === "all" ? null : parseInt(period, 10);

            try {
                const histories = await this.fetchHistories(days);
                const data = histories.length ? histories : this.allHistories.slice(-1);
                this.renderChart(data);
                this.renderStats(data);
            } catch (e) {
                console.error("기간 필터 적용 실패:", e);
            }
        });
    }

    // ── 상태 전환 ────────────────────────────────────────────

    showContent() {
        this.$overlay.style.display = "none";
        this.$content.style.display = "block";
    }

    showError() {
        this.$overlay.style.display = "none";
        this.$error.style.display   = "flex";
    }
}

document.addEventListener("DOMContentLoaded", () => {
    new ProductDetailPage(PRODUCT_ID).init();
});