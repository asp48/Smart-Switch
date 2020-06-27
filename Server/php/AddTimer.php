<?php

require_once  'db_connect.php';
$con=connect();
 
if (mysqli_connect_errno($con))
{
   echo "400";
}

else 
{
   $flag = 0;
   
   $switch = (int)$_POST["switch"];
   $stime = (int)$_POST["stime"];
   $etime = (int)$_POST["etime"];
   $date = (int)$_POST["date"];
   $uname= $_POST["username"];

   /*$uname= "PQR";
   $switch = 2;
   $stime = 2045;
   $etime = 2100;
   $date=8;*/



   if(function_exists('date_default_timezone_set')) {
    
     date_default_timezone_set("Asia/Kolkata");
    
   }

   $h= date("H ");
   $h= $h * 100;
   $m = date("i");
   $cd= date("d");
   $time = $h + $m;
   

   //echo "select Id from Status where Switch = ".$switch ." and Stime =".$stime." and Etime = ".$etime. " and Date = ".$date." and Username ='".$uname."'";

   $q = "Select * from Status where Switch = $switch order by Etime";
   $res = mysqli_query($con,$q);

   if($res){
      while($row = mysqli_fetch_array($res)){

         $set_end = $row['Etime'];
         $set_date = $row['Date'];
         /* if($set_end == 999){                                           //If someone has already turned on the switch
            

            echo "someone is controlling the switch. Sorry";
         } */
         


         if( $cd > $set_date || (($time-3)>$set_end && $cd == $set_date)){                                           //To check if the task is already completed. If so, delete.
            $q2 = "Delete from Status where Etime = $set_end and Switch = $switch";
            $res2 = mysqli_query($con,$q2);
            if(!$res2){
                echo "couldnt delete";
            }
            else{
              continue;
            }
          }


         if($stime > $set_end){continue;}                                   //One is allowed to set a timer if his ontime is greater than previously setofftime.
         else{
           $set_start = $row['Stime'];
           if($etime > $set_start){
              echo "450";
              $flag = 1;
              break;
           }
           else{ 
              continue;
           }
         }
                 
      }


      if($flag == 0){
         $q1 = "Insert into Status (Switch,Stime,Etime,Date,Username) values ($switch,$stime,$etime,$date,'$uname')";
         $res1 = mysqli_query($con,$q1);
         if(!$res1){
            echo "couldnt enter";
         }
         else{
          $q2 = "select max(Id) as id from Status";
          $res2 = mysqli_query($con,$q2);
          if($res2){
             $row = mysqli_fetch_array($res2);
             $id = $row['id'];
            echo "500_".$id;
          }
          else
            echo "450";
         }
      }
   }
   else{
      echo "Couldn't access";
   }
   
   



}

mysqli_close($con);
?>