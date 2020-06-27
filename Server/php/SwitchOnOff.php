<?php

require_once  'db_connect.php';
$con=connect();
 
if (mysqli_connect_errno($con))
{
   echo "400";
}
else{
   /*$uname="XYZ";
   $switch = 1;
   $stime = ;
   $etime = 9999;
   $date = 8; */

   $switch = (int)$_POST["switch"];
   $stime = (int)$_POST["stime"];
   $etime = (int)$_POST["etime"];
   $date = (int)$_POST["date"];
   $uname= $_POST["username"];

   if($etime == 9999){  
     $q1 = "Insert into Status (Switch,Stime,Etime,Date,Username) values ($switch,$stime,$etime,$date,'$uname')"; 
     $res1 = mysqli_query($con,$q1);
     if(!$res1){
            echo "450";
     }
     else{ 
        echo "500";
     }
   }
   else{
     $q2 = "delete from Status where Switch=".$switch." and Username= '".$uname."' and Etime = 9999"; 
     $res2 = mysqli_query($con,$q2);
     if(!$res2){
            echo "450";
     }
     else{ 
        echo "500";
     }
  
   }

   


}//else





mysqli_close($con);
?>