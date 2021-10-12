package com.duke.orca.android.kotlin.biblelockscreen.permission.view

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentPermissionRationaleDialogBinding
import com.duke.orca.android.kotlin.biblelockscreen.permission.adapter.PermissionAdapter
import com.duke.orca.android.kotlin.simpledonelist.permission.model.Permission

class PermissionRationaleDialogFragment: BaseDialogFragment<FragmentPermissionRationaleDialogBinding>() {
    private val permissionsDenied = mutableListOf<Permission>()
    private var onPermissionAllowClickListener: OnPermissionAllowClickListener? = null

    override val setWindowAnimation: Boolean
        get() = true

    interface OnPermissionAllowClickListener {
        fun onPermissionAllowClick()
        fun onPermissionDenyClick()
    }

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentPermissionRationaleDialogBinding {
        return FragmentPermissionRationaleDialogBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnPermissionAllowClickListener)
            onPermissionAllowClickListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val permissions = arrayOf(
            Permission(
                icon = R.drawable.ic_mobile_48px,
                isRequired = true,
                permissions = listOf(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
                permissionName = getString(R.string.appear_on_top),
            )
        )

        for (permission in permissions) {
            if (permission.permissions.contains(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)) {
                if (Settings.canDrawOverlays(requireContext()).not()) {
                    permissionsDenied.add(permission)
                }

                continue
            }

            permissionsDenied.add(permission)
        }

        viewBinding.recyclerView.apply {
            adapter = PermissionAdapter(permissionsDenied)
            layoutManager = LinearLayoutManagerWrapper(requireContext())
        }

        viewBinding.textViewAllow.setOnClickListener {
            onPermissionAllowClickListener?.onPermissionAllowClick()
            dismiss()
        }

        viewBinding.textViewDeny.setOnClickListener {
            onPermissionAllowClickListener?.onPermissionDenyClick()
            dismiss()
        }

        return view
    }

    companion object {
        fun permissionsGranted(context: Context): Boolean {
            if (Settings.canDrawOverlays(context).not())
                return false

            return true
        }
    }
}