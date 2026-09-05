document.addEventListener("DOMContentLoaded", () => {
    const root = document.documentElement;
    const button = document.createElement("button");

    button.type = "button";
    button.className = "theme-toggle";
    button.setAttribute("aria-label", "Toggle dark mode");

    const savedTheme = localStorage.getItem("hiretrack-theme");
    const systemDark = window.matchMedia(
        "(prefers-color-scheme: dark)"
    ).matches;

    let theme = savedTheme || (systemDark ? "dark" : "light");

    function applyTheme() {
        root.setAttribute("data-theme", theme);
        button.textContent = theme === "dark" ? "☀️" : "🌙";
        button.title = theme === "dark"
            ? "Use light mode"
            : "Use dark mode";
    }

    button.addEventListener("click", () => {
        theme = theme === "dark" ? "light" : "dark";
        localStorage.setItem("hiretrack-theme", theme);
        applyTheme();
    });

    document.body.appendChild(button);
    applyTheme();
});