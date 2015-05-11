var baseUrl = "http://qa3.intelligrape.net:11020";
var files = [];

$(function () {
    var $menu = $('#menu'),
        $menulink = $('.menu-link'),
        $menuTrigger = $('.has-submenu > a');
    $menulink.click(function (e) {
        e.preventDefault();
        $menulink.toggleClass('active');
        $menu.toggleClass('active');
    });
    $menuTrigger.click(function (e) {
        e.preventDefault();
        var $this = $(this);
        $this.toggleClass('active').next('ul').toggleClass('active');
    });
});

function trackAndUpdateUI() {
    console.log("starting");
    var $trackingTemplate = $(".complaint-tracking-template");

    var complaintId = $(".formInputTrack").val().trim();
    if (complaintId == "" || "Complaint Number" == complaintId) {
        alert("Please enter complaint id");
    }
    else {
        $(".trackContainer .progressSteps").hide();
        $(".trackContainer .progressInfo").hide();
        $(".trackContainer .breadcrumbsHolder").hide();
        $(".trackContainer .complaint-id").hide();
        $(".trackContainer .horizontal-break").hide();
        jQuery.ajax({
            url: baseUrl + '/bin/mobile/v1/complaint/track.json',
            data: {"complaintId": complaintId},
            dataType: 'json',
            method: 'GET',
            success: function (data, textStatus, jqXHR) {
                var stepsCompleted;
                $.each(data, function(idx, element){
                    var statusMap = {"step1": "Received on " + element.receivedDate, "step2": "Started", "step3": "Completed on " + element.completedDate};
                    if (typeof element.status == "undefined" || element.status == 0) {
                        stepsCompleted = 1
                    }
                    else if (element.status == -1) {
                        stepsCompleted = 3
                    }
                    else {
                        stepsCompleted = 2
                    }
                    var $clonedTemplate = $trackingTemplate.clone();
                    $clonedTemplate.find(".complaint-id").text(element.complaintNumber);
                    var $progressSteps = $clonedTemplate.find(".progressSteps");
                    var $progressInfo = $clonedTemplate.find(".progressInfo");
                    $progressSteps.find("img").removeClass("active").addClass("inactive");
                    for (var count = 1; count <= stepsCompleted; count++) {
                        $clonedTemplate.find(".breadcrumb li").eq(count-1).find("a").addClass("done-state");
                        $progressSteps.find("img").eq(count - 1).removeClass("inactive").addClass("active");
                        $progressSteps.find(".step" + count).find(".stepText").text(statusMap["step" + count]);
                    }
                    $progressInfo.text(element.complaint);
                    $(".trackContainer").append($clonedTemplate.children());
                })
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $(".trackContainer .progressSteps").hide();
                $(".trackContainer .progressInfo").hide();
                $(".trackContainer .breadcrumbsHolder").hide();
                $(".trackContainer .complaint-id").hide();
                $(".trackContainer .horizontal-break").hide();
                alert("Complaint not found");
            }
        });
        console.log("shown");
    }
}

function showSpinner(){
    $(".loader").show();
}

function hideSpinner(){
    $(".loader").hide();
}

function submitComplaintForm() {
    showSpinner();
    var $file = $(".file-complaint");
    var $complaintForm = $file.find("form[name=complaint-form]");
    var validForm = true;
    var errorFields = [];
    var postData = $complaintForm.serializeArray();
    var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    var numberPattern = /^[0-9]*$/;
    $.each($complaintForm.find("input,textarea, select"), function () {
        if ($(this).val().trim() == "" && $(this).attr("type") != "file" && $(this).attr("type") != "hidden") {
            errorFields.push($(this).data("nice-name"));
            $(this).parent("li").find("label").addClass("errorField");
            validForm = false;
        }
        else{
            $(this).parent("li").find("label").removeClass("errorField");
        }
    });

    if(!emailPattern.test($complaintForm.find("input[name=email]").val())){
        errorFields.push("Email");
        $complaintForm.find("input[name=email]").prev('label').addClass('errorField');
        validForm = false;
    }

    if($complaintForm.find("input[name=mobile]").val().length != 10 || !numberPattern.test($complaintForm.find("input[name=mobile]").val())){
        errorFields.push("Mobile");
        $complaintForm.find("input[name=mobile]").prev('label').addClass('errorField');
        validForm = false;
    }

    if(!numberPattern.test($complaintForm.find("input[name=pincode]").val())){
        errorFields.push("Pin Code");
        $complaintForm.find("input[name=pincode]").prev('label').addClass('errorField');
        validForm = false;
    }

    if(files.length > 0){
        postData.push({name:"imagePath", value:files[0].data});
        files = [];
    }

    if (!validForm) {
        $file.find(".form-error").text("*Error in fields : "+errorFields.join(', '));
        $file.find(".form-error").show();
        hideSpinner();
    }
    else {
        $file.find(".form-error").hide();
        var formURL = baseUrl + "/bin/mobile/v1/complaint/submit.json";
        $.ajax(
            {
                url: formURL,
                type: "POST",
                dataType: 'json',
                data: postData,
                success: function (data, textStatus, jqXHR) {
                    $file.find(".otp-verification").find("input[name=mobile]").val($complaintForm.find("input[name=mobile]").val());
                    $complaintForm.trigger("reset");
                    $complaintForm.parent(".faqWrapper").hide();
                    $file.find(".faqWrapper").last().show();
                    $file.find(".complaint-confirmation").find("span").text("Congratulations!!! Your complaint has been filed successfully. Your complaint id is " + data.complaintNumber);
                    console.log("submitted successfully");
                    hideSpinner();
                    //data: return data from server
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    hideSpinner();
                    alert("There was some error filing the complaint");
                    console.log("error in submission", errorThrown);
                    //if fails
                }
            });
    }

}

function checkOtp(){
    var $file = $(".file-complaint");
    var mobileNumber = $(".otp-verification").find("input[name=mobile]").val();
    var userEnteredOtp = $("input[name=otp]").val();
    jQuery.ajax({
        url: baseUrl+'/bin/mobile/v1/otp/authenticate.json',
        data: {"otp": userEnteredOtp, "mobile":mobileNumber},
        method: 'GET',
        complete: function(xhr, textStatus) {
            if(xhr.status == 200){
                $file.find(".faqWrapper").last().find(".otp-verification").hide();
                $file.find(".faqWrapper").last().find(".complaint-confirmation").show();
            }
            else{
                alert("Verification Failed")
            }
        }
    });
    return false;
}

function onDeviceReady() {
    $(document).on("change", "#complaintImage", handleFileSelect)
}

function handleFileSelect(evt) {
    $.each(event.target.files, function (index, file) {
        if(file.size / (1024*1024) > 1 ){
            alert("File too large. Please upload file less than 1 MB");
        }
        else{
            var reader = new FileReader();
            reader.onload = function (event) {
                object = {};
                object.filename = file.name;
                console.log("inside target", event.target.result);
                object.data = event.target.result;
                files.push(object);
            };
            reader.readAsDataURL(file);
        }
    });
}


document.addEventListener("deviceready", onDeviceReady, false);



