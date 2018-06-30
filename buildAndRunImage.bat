s2i build . feethru_os_image feethru_build_img
docker run -p 8080:8080 feethru_build_img
