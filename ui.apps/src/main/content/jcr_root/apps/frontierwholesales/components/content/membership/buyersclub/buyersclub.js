use(function () {
  var param = this.params;
    var paramSplit = param.split('&');

    var emailSplit = paramSplit[1].split('=');
    if(emailSplit[0] == 'email'){
         out.println('email is '+emailSplit[1]);
        return { email:emailSplit[1]
               };
    }
    return "";
});