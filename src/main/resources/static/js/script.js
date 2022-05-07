async function getData() {
    const data = await fetch('http://localhost:8080/api/videos')
    const dataJson = await data.json()
    console.log(dataJson)
    const div = document.querySelector("div")

    for (let d of dataJson) {
        div.insertAdjacentHTML(
            'beforeend',
            `<img src='/uploadVideo/${d.fileName}' alt='...' title='${d.title}'><br/>`)
    }
}
getData()