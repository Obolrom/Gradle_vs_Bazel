package com.romix.feature.feat507

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat507Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat507UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat507FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat507UserSummary
)

data class Feat507UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat507NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat507Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat507Config = Feat507Config()
) {

    fun loadSnapshot(userId: Long): Feat507NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat507NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat507UserSummary {
        return Feat507UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat507FeedItem> {
        val result = java.util.ArrayList<Feat507FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat507FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat507UiMapper {

    fun mapToUi(model: List<Feat507FeedItem>): Feat507UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat507UiModel(
            header = UiText("Feat507 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat507UiModel =
        Feat507UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat507UiModel =
        Feat507UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat507UiModel =
        Feat507UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat507Service(
    private val repository: Feat507Repository,
    private val uiMapper: Feat507UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat507UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat507UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat507UserItem1(val user: CoreUser, val label: String)
data class Feat507UserItem2(val user: CoreUser, val label: String)
data class Feat507UserItem3(val user: CoreUser, val label: String)
data class Feat507UserItem4(val user: CoreUser, val label: String)
data class Feat507UserItem5(val user: CoreUser, val label: String)
data class Feat507UserItem6(val user: CoreUser, val label: String)
data class Feat507UserItem7(val user: CoreUser, val label: String)
data class Feat507UserItem8(val user: CoreUser, val label: String)
data class Feat507UserItem9(val user: CoreUser, val label: String)
data class Feat507UserItem10(val user: CoreUser, val label: String)

data class Feat507StateBlock1(val state: Feat507UiModel, val checksum: Int)
data class Feat507StateBlock2(val state: Feat507UiModel, val checksum: Int)
data class Feat507StateBlock3(val state: Feat507UiModel, val checksum: Int)
data class Feat507StateBlock4(val state: Feat507UiModel, val checksum: Int)
data class Feat507StateBlock5(val state: Feat507UiModel, val checksum: Int)
data class Feat507StateBlock6(val state: Feat507UiModel, val checksum: Int)
data class Feat507StateBlock7(val state: Feat507UiModel, val checksum: Int)
data class Feat507StateBlock8(val state: Feat507UiModel, val checksum: Int)
data class Feat507StateBlock9(val state: Feat507UiModel, val checksum: Int)
data class Feat507StateBlock10(val state: Feat507UiModel, val checksum: Int)

fun buildFeat507UserItem(user: CoreUser, index: Int): Feat507UserItem1 {
    return Feat507UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat507StateBlock(model: Feat507UiModel): Feat507StateBlock1 {
    return Feat507StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat507UserSummary> {
    val list = java.util.ArrayList<Feat507UserSummary>(users.size)
    for (user in users) {
        list += Feat507UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat507UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat507UiModel {
    val summaries = (0 until count).map {
        Feat507UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat507UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat507UiModel> {
    val models = java.util.ArrayList<Feat507UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat507AnalyticsEvent1(val name: String, val value: String)
data class Feat507AnalyticsEvent2(val name: String, val value: String)
data class Feat507AnalyticsEvent3(val name: String, val value: String)
data class Feat507AnalyticsEvent4(val name: String, val value: String)
data class Feat507AnalyticsEvent5(val name: String, val value: String)
data class Feat507AnalyticsEvent6(val name: String, val value: String)
data class Feat507AnalyticsEvent7(val name: String, val value: String)
data class Feat507AnalyticsEvent8(val name: String, val value: String)
data class Feat507AnalyticsEvent9(val name: String, val value: String)
data class Feat507AnalyticsEvent10(val name: String, val value: String)

fun logFeat507Event1(event: Feat507AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat507Event2(event: Feat507AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat507Event3(event: Feat507AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat507Event4(event: Feat507AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat507Event5(event: Feat507AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat507Event6(event: Feat507AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat507Event7(event: Feat507AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat507Event8(event: Feat507AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat507Event9(event: Feat507AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat507Event10(event: Feat507AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat507Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat507Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat507Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat507Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat507Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat507Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat507Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat507Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat507Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat507Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat507(u: CoreUser): Feat507Projection1 =
    Feat507Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat507Projection1> {
    val list = java.util.ArrayList<Feat507Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat507(u)
    }
    return list
}
