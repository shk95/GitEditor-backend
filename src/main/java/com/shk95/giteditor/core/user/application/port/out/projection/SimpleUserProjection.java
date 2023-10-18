package com.shk95.giteditor.core.user.application.port.out.projection;

public interface SimpleUserProjection extends UserIdProjection {

	String getUsername();

	String getDefaultEmail();

	String getProfileImageUrl();
}
