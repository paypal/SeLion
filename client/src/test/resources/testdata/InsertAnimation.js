var css =
  "#loader {" +
            "display: none;" +
            "animation: spin 2s linear infinite;" +
            "border-color: #555 #f3f3f3 #f3f3f3;" +
            "border-radius: 50%;" +
            "border-style: solid;" +
            "border-width: 5px;" +
            "height: 30px;" +
            "width: 30px;"+
  "}" +
   "@keyframes spin {" +
                    "0% {transform: rotate(0deg);}" +
                    "100% {transform: rotate(360deg);}" +
   "}";

var timeout = parseInt(arguments[0]) || 5000;
var newPageTitle = arguments[1] || "TestPage After Animation";

style = document.createElement("style");
style.appendChild(document.createTextNode(css));
document.head.appendChild(style);

var spinner = document.createElement("div");
spinner.id = "loader";
document.body.appendChild(spinner);

function doAnimate() {
  document.getElementById("loader").style.display = "block";
  setTimeout(update, timeout);
}

function update() {
  document.getElementById("loader").style.display = "none";
  document.title = newPageTitle;
}

var animationControlButton = document.createElement("input");
animationControlButton.id = "animation";
animationControlButton.type = "submit";
animationControlButton.value = "Show Spinner";
animationControlButton.onclick = doAnimate;
document.body.appendChild(animationControlButton);
