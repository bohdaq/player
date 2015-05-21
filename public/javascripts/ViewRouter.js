var ViewRouter = function() {
    ViewRouter.previousViewId =  window.location.hash.substring(1) || "listOfAudios";
    this.showView(ViewRouter.previousViewId);
};
ViewRouter.prototype.routeTo = function(idOfElement) {
    //this if responsible for showing default view - "listOfAudios"
    if($(event.target).hasClass('activeTopButton')){
        $(event.target).removeClass('activeTopButton');

        this.hideView(ViewRouter.previousViewId);
        this.showView("listOfAudios");
        window.history.pushState("listOfAudios", "listOfAudios", "/#");
        return;
    }
    $(".topBtn").removeClass('activeTopButton');
    $(event.target).addClass('activeTopButton');

    this.hideView(ViewRouter.previousViewId);
    this.showView(idOfElement);
    window.history.pushState(idOfElement, idOfElement, "/#" + idOfElement);
};
ViewRouter.prototype.showView = function(idOfElement){
    document.getElementById(idOfElement).style.display = "block";
    ViewRouter.previousViewId = idOfElement;
    console.log("Shown " + idOfElement);
}
ViewRouter.prototype.hideView = function(idOfElement){
    document.getElementById(idOfElement).style.display = "none";
    console.log("Hide " + idOfElement);
}