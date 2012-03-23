<?

$data_dir = (dirname(dirname(__FILE__))) . "/logdata";

if (!is_dir($data_dir)) {
    die("{$data_dir} not found\n");
}

$count = 500000;

$files_to_be_generated = 20;

for ($j = 0; $j < $files_to_be_generated; $j++) {
    $file_location = "{$data_dir}/" . sha1(time() . mt_rand());
    $fp = fopen($file_location, "w");

    if ($fp) {
        for ($i = 0; $i < $count; $i++) {
            $email_string = sha1(time() . mt_rand())."@mailinator.com";
            $data = array(
                'key' => $email_string,
                'value' => sha1(time() . mt_rand())
            );
            $json = str_replace('\/', '/', json_encode($data));
            fwrite($fp, $json."\n");
        }
    }
}
