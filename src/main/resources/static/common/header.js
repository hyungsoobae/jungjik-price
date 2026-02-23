document.getElementById('header-search-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const keyword = document.getElementById('header-search').value.trim();
    const source = document.getElementById('header-source').value;
    const params = new URLSearchParams();
    if (keyword) params.set('keyword', keyword);
    if (source) params.set('source', source);
    window.location.href = `/products${params.toString() ? '?' + params.toString() : ''}`;
});