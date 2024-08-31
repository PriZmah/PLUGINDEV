#!/bin/bash

nombre_archivo="ffautils-0.3-ALPHA.jar"
viejo_archivo="/home/ubuntu/wodserver/plugins/$nombre_archivo"
nuevo_archivo="/home/prizmah/Escritorio/FFAUTILS/ffautils/target/$nombre_archivo"
viejo_hash="$(ssh servidor-oci "md5sum $viejo_archivo | awk '{print \$1}'")"
nuevo_hash="$(md5sum $nuevo_archivo | awk '{print $1}')"
comando="restart"

echo $viejo_hash
echo $nuevo_hash
if [ "$nuevo_hash" == "$viejo_hash" ]; then
    echo "Los archivos son idénticos."
else
    echo "Sobreescribiendo archivo"
    scp "$nuevo_archivo" servidor-oci:/home/ubuntu/wodserver/plugins/
    if [ $? -eq 0 ]; then
        echo "Archivo sobreescrito exitosamente."
        ssh servidor-oci "screen -S wodinstance1 -p 0 -X stuff \"$comando\n\""

        if [ $? -eq 0 ]; then
            echo "Comando enviado exitosamente a la sesión de screen."
        else
            echo "Error al enviar el comando a la sesión de screen."
        fi
    else
        echo "Error al copiar el archivo."
    fi
fi


