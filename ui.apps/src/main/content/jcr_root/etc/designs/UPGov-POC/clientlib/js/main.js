$(function(){
    var $menu = $('#menu'),
        $menulink = $('.menu-link'),
        $menuTrigger = $('.has-submenu > a');
    $menulink.click(function(e) {
        e.preventDefault();
        $menulink.toggleClass('active');
        $menu.toggleClass('active');
    });
    $menuTrigger.click(function(e) {
        e.preventDefault();
        var $this = $(this);
        $this.toggleClass('active').next('ul').toggleClass('active');
    });});

var windowWidth = $(window).width()
if(windowWidth>479){
    $('.commonMan').flexslider({
        animation: "slide",
        controlNav: "true",
        itemWidth: 400,
        minItems: 2,
        maxItems: 2,
        itemMargin: 10
    });
}
if(windowWidth<479){
    $('.commonMan').flexslider({
        animation: "slide",
        animationLoop: false,
        controlNav: false,
        itemWidth: 400,
        minItems: 1,
        maxItems: 1,
        directionNav: true
    });
}
$(".clicker").click(function(){
    $(".content").toggle();
    $(".clicker").removeClass("active");
    $(this).addClass("active");
    $(this).next().slideDown();
});
$('.fancybox').fancybox({
        iframe:{scrolling:"no"},
        padding:0,
        beforeLoad :function(){
            $(".overlay").addClass("open");
        },
        afterClose : function(){
            $(".overlay").removeClass("open");
        }
    }
);
$('.fancyboxLogin').fancybox({
        padding:5,
        minHeight:545,
        width:900,
        beforeLoad :function(){
            $(".overlay").addClass("open");
        },
        afterClose : function(){
            $(".overlay").removeClass("open");
        }
    }
);

