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