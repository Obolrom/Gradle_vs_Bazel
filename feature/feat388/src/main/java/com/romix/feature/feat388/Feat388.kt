package com.romix.feature.feat388

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat388Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat388UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat388FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat388UserSummary
)

data class Feat388UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat388NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat388Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat388Config = Feat388Config()
) {

    fun loadSnapshot(userId: Long): Feat388NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat388NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat388UserSummary {
        return Feat388UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat388FeedItem> {
        val result = java.util.ArrayList<Feat388FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat388FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat388UiMapper {

    fun mapToUi(model: List<Feat388FeedItem>): Feat388UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat388UiModel(
            header = UiText("Feat388 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat388UiModel =
        Feat388UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat388UiModel =
        Feat388UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat388UiModel =
        Feat388UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat388Service(
    private val repository: Feat388Repository,
    private val uiMapper: Feat388UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat388UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat388UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat388UserItem1(val user: CoreUser, val label: String)
data class Feat388UserItem2(val user: CoreUser, val label: String)
data class Feat388UserItem3(val user: CoreUser, val label: String)
data class Feat388UserItem4(val user: CoreUser, val label: String)
data class Feat388UserItem5(val user: CoreUser, val label: String)
data class Feat388UserItem6(val user: CoreUser, val label: String)
data class Feat388UserItem7(val user: CoreUser, val label: String)
data class Feat388UserItem8(val user: CoreUser, val label: String)
data class Feat388UserItem9(val user: CoreUser, val label: String)
data class Feat388UserItem10(val user: CoreUser, val label: String)

data class Feat388StateBlock1(val state: Feat388UiModel, val checksum: Int)
data class Feat388StateBlock2(val state: Feat388UiModel, val checksum: Int)
data class Feat388StateBlock3(val state: Feat388UiModel, val checksum: Int)
data class Feat388StateBlock4(val state: Feat388UiModel, val checksum: Int)
data class Feat388StateBlock5(val state: Feat388UiModel, val checksum: Int)
data class Feat388StateBlock6(val state: Feat388UiModel, val checksum: Int)
data class Feat388StateBlock7(val state: Feat388UiModel, val checksum: Int)
data class Feat388StateBlock8(val state: Feat388UiModel, val checksum: Int)
data class Feat388StateBlock9(val state: Feat388UiModel, val checksum: Int)
data class Feat388StateBlock10(val state: Feat388UiModel, val checksum: Int)

fun buildFeat388UserItem(user: CoreUser, index: Int): Feat388UserItem1 {
    return Feat388UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat388StateBlock(model: Feat388UiModel): Feat388StateBlock1 {
    return Feat388StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat388UserSummary> {
    val list = java.util.ArrayList<Feat388UserSummary>(users.size)
    for (user in users) {
        list += Feat388UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat388UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat388UiModel {
    val summaries = (0 until count).map {
        Feat388UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat388UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat388UiModel> {
    val models = java.util.ArrayList<Feat388UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat388AnalyticsEvent1(val name: String, val value: String)
data class Feat388AnalyticsEvent2(val name: String, val value: String)
data class Feat388AnalyticsEvent3(val name: String, val value: String)
data class Feat388AnalyticsEvent4(val name: String, val value: String)
data class Feat388AnalyticsEvent5(val name: String, val value: String)
data class Feat388AnalyticsEvent6(val name: String, val value: String)
data class Feat388AnalyticsEvent7(val name: String, val value: String)
data class Feat388AnalyticsEvent8(val name: String, val value: String)
data class Feat388AnalyticsEvent9(val name: String, val value: String)
data class Feat388AnalyticsEvent10(val name: String, val value: String)

fun logFeat388Event1(event: Feat388AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat388Event2(event: Feat388AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat388Event3(event: Feat388AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat388Event4(event: Feat388AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat388Event5(event: Feat388AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat388Event6(event: Feat388AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat388Event7(event: Feat388AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat388Event8(event: Feat388AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat388Event9(event: Feat388AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat388Event10(event: Feat388AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat388Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat388Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat388Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat388Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat388Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat388Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat388Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat388Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat388Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat388Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat388(u: CoreUser): Feat388Projection1 =
    Feat388Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat388Projection1> {
    val list = java.util.ArrayList<Feat388Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat388(u)
    }
    return list
}
