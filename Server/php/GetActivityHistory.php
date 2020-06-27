<?php
require_once  'db_connect.php';
$con=connect();  
if (mysqli_connect_errno($con))
{
   echo "400";
}
else {
  
   $uname=$_POST["uname"];
   //$uname= "XYZ";
   if(function_exists('date_default_timezone_set')) {
      date_default_timezone_set("Asia/Kolkata");
   }

   $h= date("H ");
   $h= $h * 100;
   $m = date("i");
   $time = $h + $m;
   $cd= date("d");


   $q = "Select * from Status where Username = '".$uname."' order by Etime";
   $res = mysqli_query($con,$q);

   if($res){

      $result=array();

      while($row = mysqli_fetch_array($res)){
         $set_end = $row['Etime'];
         $id= $row['Id'];
         $set_date= $row['Date'];

         //echo "Delete from Status where Id = ".$id."";
         
        if( $cd > $set_date || ((($time-3)>$set_end) && ($cd == $set_date))){                                           //To check if the task is already completed. If so, delete.
            $q2 = "Delete from Status where Id = ".$id."";
            $res2 = mysqli_query($con,$q2);
            if(!$res2){
                echo "acouldnt delete";
            }
            else{
              
              continue;
            }
          }
          else{
             array_push($result,array('id'=>$row[0],'switch'=>$row[1],'stime'=>$row[2],'etime'=>$row[3]));
                     
          }
       }
       echo json_encode(array('Results'=>$result));
    }
    else{
      echo "couldnt fetch user details";
    }
}
mysqli_close($con);
?>