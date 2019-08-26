package com.utility;

import java.util.List;

/**
 * <h>PermissionsListener</h>
 * Created by Ali on 11/29/2017.
 */

public interface PermissionsListener
{
    void onExplanationNeeded(List<String> permissionsToExplain);

    void onPermissionResult(boolean granted);
}
