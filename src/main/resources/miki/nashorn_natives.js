const Native = function () {
    const File = Java.type("java.io.File");
    let nativeOut = print;

    return {
        print: function (obj) {
            nativeOut(JSON.stringify({
                type: "print",
                message: obj,
                new_line: false
            }));
        },

        println: function (obj) {
            nativeOut(JSON.stringify({
                type: "print",
                message: obj,
                new_line: true
            }));
        },

        getFile: function (path) {
            return Java.type("co.casterlabs.miki.MikiUtil").getFile(path);
        },

        fileExists: function (path) {
            return new File(path).exists();
        },

        createFile: function (path) {
            return new File(path).createNewFile();
        },

        setHeader: function (key, value) {
            nativeOut(JSON.stringify({
                type: "header",
                key: key,
                value: value
            }));
        },

        setMime: function (mime) {
            nativeOut(JSON.stringify({
                type: "mime",
                mime: mime
            }));
        },

        setStatus: function (code) {
            nativeOut(JSON.stringify({
                type: "status",
                status: code
            }));
        },

        webRequest: function (url, method, body) {
            return JSON.parse(Java.type("co.casterlabs.miki.MikiUtil").sendHttp(method, body, url));
        },

        write: function (file, content) {
            Java.type("co.casterlabs.miki.MikiUtil").writeFile(file, JSON.stringify(content));
        },

        log: function (level, obj) {
            nativeOut(JSON.stringify({
                type: "log",
                level: level,
                message: obj
            }));
        }
    };
}();

print = undefined;