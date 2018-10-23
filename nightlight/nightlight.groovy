definition(
    name: "Nightlight",
    namespace: "smartthings",
    author: "Matthew Shurtz",
    description: "Keep lights on after dark",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png"
)

preferences {
	section("Keep these lights on after dark...") {
		input "lights", "capability.switch", multiple: true
	}
    section("Turn on automatically at sunset?") {
    	input name: "autoSunset", type: "bool", title: "Auto on at sunset", required: true
    }
    section("Turn off automatically at sunrise?") {
    	input name: "autoSunrise", type: "bool", title: "Auto off at sunrise", required: true
    }
}

def installed() {
	registerSubscriptions()
}

def updated() {
	unsubscribe()
	registerSubscriptions()
}

def registerSubscriptions() {
	subscribe(lights, "switch.off", lightHandler)
	subscribe(location, "sunset", sunsetHandler)
    subscribe(location, "sunrise", sunriseHandler)
}

def lightHandler(evt) {
	def time = getSunriseAndSunset()
    def sunrise = time.sunrise
    def sunset = time.sunset
	def between = timeOfDayIsBetween(sunset, sunrise, new Date(), location.timeZone)
    if (between) {
        lights.on()
    }
}

def sunsetHandler(evt) {
	if (autoSunset) {
    	lights.on()
    }
}

def sunriseHandler(evt) {
	if (autoSunrise) {
    	lights.off()
    }
}