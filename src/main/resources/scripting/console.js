const console = function() {
	let nativePrint = print;

	return { // TODO move to V8 and make this class nonexistient.
		info: function(obj) {
			nativePrint("!I" + obj);
		},
	
		log: function(obj) {
			nativePrint("!I" + obj);
		},
	
		warn: function(obj) {
			nativePrint("!W" + obj);
		},
	
		severe: function(obj) {
			nativePrint("!S" + obj);
		},
	
		error: function(obj) {
			nativePrint("!S" + obj);
		},
	
		debug: function(obj) {
			nativePrint("!D" + obj);
		}
	
	};
}();
