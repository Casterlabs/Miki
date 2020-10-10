const console = function () {

	return {
		info: function (obj) {
			Native.log("INFO", obj);
		},

		log: function (obj) {
			Native.log("INFO", obj);
		},

		warn: function (obj) {
			Native.log("WARNING", obj);
		},

		severe: function (obj) {
			Native.log("SEVERE", obj);
		},

		error: function (obj) {
			Native.log("SEVERE", obj);
		},

		debug: function (obj) {
			Native.log("DEBUG", obj);
		}

	};
}();
