package com.shk95.giteditor.domain.model.oauth;


import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.model.AbstractOAuth2UserInfo;

import java.util.Map;

@Deprecated
public class FacebookOAuth2UserInfo extends AbstractOAuth2UserInfo {
	public FacebookOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes, ProviderType providerType) {
		super(attributes, additionalAttributes, providerType);
	}

	@Override
	public String getId() {
		return (String) super.getAttributes().get("id");
	}

	@Override
	public String getLoginId() {
		return null;
	}

	@Override
	public String getName() {
		return (String) super.getAttributes().get("name");
	}

	@Override
	public String getEmail() {
		return (String) super.getAttributes().get("email");
	}

	@Override
	public String getImageUrl() {
		return (String) super.getAttributes().get("imageUrl");
	}

	@Override
	public String getAccessToken() {
		return null;
	}

    /*
    @Override
    public String getImageUrl() {
        if(attributes.containsKey("picture")) {
            Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
            if(pictureObj.containsKey("data")) {
                Map<String, Object>  dataObj = (Map<String, Object>) pictureObj.get("data");
                if(dataObj.containsKey("url")) {
                    return (String) dataObj.get("url");
                }
            }
        }
        return null;
    }
    */
}
