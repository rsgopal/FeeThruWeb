docker tag feethru_build_img rsgopal/feethru_build_img
docker push rsgopal/feethru_build_img
oc delete all -l app=feethrubuildimg
oc new-app rsgopal/feethru_build_img
oc expose svc/feethrubuildimg