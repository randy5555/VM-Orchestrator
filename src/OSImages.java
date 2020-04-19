
public class OSImages {
	static String OSImageArray[] = {
			"",
			"CentOS-7-x86_64-DVD-1908.iso",
			"CentOS-8.1.1911-x86_64-dvd1.iso",
			"debian-10.3.0-amd64-netinst.iso",
			"debian-9.12.0-amd64-netinst.iso",
			"Fedora-Server-dvd-x86_64-31-1.9.iso",
			"Fedora-Server-dvd-x86_64-32_Beta-1.2.iso",
			"Fedora-Silverblue-ostree-x86_64-31-1.9.iso",
			"Fedora-Workstation-Live-x86_64-31-1.9.iso",
			"Fedora-Workstation-Live-x86_64-32_Beta-1.2.iso",
			"FreeBSD-11.3-RELEASE-amd64-dvd1.iso",
			"FreeBSD-11.3-STABLE-amd64-20200409-r359722-disc1.iso",
			"FreeBSD-12.1-RELEASE-amd64-dvd1.iso",
			"FreeBSD-13.0-CURRENT-amd64-20200409-r359731-disc1.iso",
			"ubuntu-14.04.6-desktop-amd64.iso",
			"ubuntu-14.04.6-server-amd64.iso",
			"ubuntu-16.04.6-desktop-amd64.iso",
			"ubuntu-16.04.6-server-amd64.iso",
			"ubuntu-18.04.4-desktop-amd64.iso",
			"ubuntu-19.10-desktop-amd64.iso",
			"ubuntu-20.04-beta-desktop-amd64.iso",
			"ubuntu-20.04-beta-live-server-amd64.iso"
	};
	
	 static String getOSImageName(int imageid) {
		 if(imageid > getNumberOfImages()) {
			 imageid = 0;
		 }
		return OSImageArray[imageid];
	}
	
	 static int getNumberOfImages( ) {
		return OSImageArray.length;
	}
	
	
}
