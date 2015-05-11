$( document ).ready(function() {
    $("input[aria-label='Number']").attr("placeholder",$('#mobile').val());
    $("input[aria-label='Number']").val($('#mobile').val());
    //  $("input[aria-label='Number']").attr("read-only","true");

    $("input[aria-label='Name']").attr("placeholder",$('#fullname').val());
    $("input[aria-label='Name']").val($('#fullname').val());
    //$("input[aria-label='Name']").attr("disabled","true");
});