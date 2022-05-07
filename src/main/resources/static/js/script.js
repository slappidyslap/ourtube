async function getData() {
    const data = await fetch('http://localhost:8080/api/videos')
    const dataJson = await data.json()
    console.log(dataJson)
    const div = document.querySelector("div")

    for (let d of dataJson) {
        div.insertAdjacentHTML(
            'beforeend',
            `<img src='/video/${d.fileName}' alt='...' title='${d.title}'><br/>`)
    }
}


const div = document.querySelector(".video-list_item")
div.addEventListener("mouseover", (e) => {
    if (e.target.closest('img')) {
        const video = document.createElement('video')
        video.src = '/video/video.mp4'
        video.autoplay = true
        video.width = 500
        video.loop = true
        console.log(e.target)
        console.log(video)
        console.log(e.type)
        div.replaceChild(video, e.target)
    }
});
div.addEventListener("mouseout", (e) => {
    if (e.target.closest('video')) {
        const img = document.createElement('img')
        img.src = '/video/Screenshot_1.png'
        img.alt = '...'
        img.width = 500
        console.log(e.type)
        div.replaceChild(img, e.target)
    }
});