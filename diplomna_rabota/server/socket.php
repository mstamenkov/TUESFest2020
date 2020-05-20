<?php
$serv=stream_socket_server("tcp://78.130.176.59:5000",$errno,$errstr) or die("create server failed");
$flagGPSIMU = 0;
$flagShockRfid = 0;
for($i=0; $i < 2; $i ++) {
    if (pcntl_fork() == 0 ) {
        while(1) {
            $conn = stream_socket_accept($serv);
            if ($conn == false) continue;
            else{
                while(1){
                        $request = fread($conn,100);
                        fwrite(STDOUT, $request);
                        $gpsId = strpos($request,'GNGGA');
                        if($gpsId != false){
                            $gpsIdSub = substr($request,93,5);
                            $imu = substr($request,98,1);
                            fwrite(STDOUT, "Id is:");
                            fwrite(STDOUT, $gpsIdSub);
                            $longIndex = $gpsId+17;
                            $langIndex = $gpsId+29;
                            $long = substr($request,$longIndex,9);
                            $lang = substr($request,$langIndex,10);
                            $longDecimal = substr($long,0,2) + (substr($long,2)/60);
                            $langDecimal = substr($lang,0,3) + (substr($lang,3)/60);
                            fwrite(STDOUT, $longDecimal);
                            fwrite(STDOUT, " ");
                            fwrite(STDOUT, $langDecimal);
                            fwrite(STDOUT, "\n");
                            $flagGPSIMU = 1;
                        }else if(strlen($request) == 7){
                            $idShock = substr($request,0,5);
                            $shock = substr($request,5,1);
                            $rfid = substr($request,6,1);
                            $flagShockRfid =1;
                            fwrite(STDOUT, $shock);
                            fwrite(STDOUT, $rfid);
                        }/*else if(strlen($request) == 6){
                            $idIMU = substr($request,0,5);
                            $imu = substr($request,5,1);
                            fwrite(STDOUT, $imu);
                            $flagIMU = 1;
                        }*/

                        if($flagGPSIMU == 1){
                            require_once "./config.php";
                            $stmt = ("INSERT INTO moduleData(eventTime, moduleId, latitude, longitude,imuEvent) values(NOW(),$gpsIdSub,$langDecimal,$longDecimal,$imu)");
                            //fix long and latitude !!!!!!!!!!!!!!
                            if (mysqli_query($con, $stmt)) {
                                echo "New record created successfully";
                            } else {
                                echo "Error: " . $stmt . "<br>" . $con->error;
                            }
                            $flagGPSIMU=0;

                        }
                        if($flagShockRfid == 1){
                            require_once "./config.php";
                            $stmt = ("INSERT INTO moduleData(eventTime, moduleId, shockEvent, rfidEvent) values(NOW(),$idShock,$shock,$rfid)");
                            if (mysqli_query($con, $stmt)) {
                                echo "New record created successfully";
                            } else {
                                echo "Error: " . $stmt . "<br>" . $con->error;
                            }
                            $flagGPSIMU=0;
                        }
                        if($conn == false) break;
                }
            }
            fclose($conn);
        } exit(0);
    }
}
