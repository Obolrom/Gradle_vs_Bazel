package com.romix.feature.feat594

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat594Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat594UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat594FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat594UserSummary
)

data class Feat594UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat594NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat594Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat594Config = Feat594Config()
) {

    fun loadSnapshot(userId: Long): Feat594NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat594NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat594UserSummary {
        return Feat594UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat594FeedItem> {
        val result = java.util.ArrayList<Feat594FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat594FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat594UiMapper {

    fun mapToUi(model: List<Feat594FeedItem>): Feat594UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat594UiModel(
            header = UiText("Feat594 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat594UiModel =
        Feat594UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat594UiModel =
        Feat594UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat594UiModel =
        Feat594UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat594Service(
    private val repository: Feat594Repository,
    private val uiMapper: Feat594UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat594UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat594UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat594UserItem1(val user: CoreUser, val label: String)
data class Feat594UserItem2(val user: CoreUser, val label: String)
data class Feat594UserItem3(val user: CoreUser, val label: String)
data class Feat594UserItem4(val user: CoreUser, val label: String)
data class Feat594UserItem5(val user: CoreUser, val label: String)
data class Feat594UserItem6(val user: CoreUser, val label: String)
data class Feat594UserItem7(val user: CoreUser, val label: String)
data class Feat594UserItem8(val user: CoreUser, val label: String)
data class Feat594UserItem9(val user: CoreUser, val label: String)
data class Feat594UserItem10(val user: CoreUser, val label: String)

data class Feat594StateBlock1(val state: Feat594UiModel, val checksum: Int)
data class Feat594StateBlock2(val state: Feat594UiModel, val checksum: Int)
data class Feat594StateBlock3(val state: Feat594UiModel, val checksum: Int)
data class Feat594StateBlock4(val state: Feat594UiModel, val checksum: Int)
data class Feat594StateBlock5(val state: Feat594UiModel, val checksum: Int)
data class Feat594StateBlock6(val state: Feat594UiModel, val checksum: Int)
data class Feat594StateBlock7(val state: Feat594UiModel, val checksum: Int)
data class Feat594StateBlock8(val state: Feat594UiModel, val checksum: Int)
data class Feat594StateBlock9(val state: Feat594UiModel, val checksum: Int)
data class Feat594StateBlock10(val state: Feat594UiModel, val checksum: Int)

fun buildFeat594UserItem(user: CoreUser, index: Int): Feat594UserItem1 {
    return Feat594UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat594StateBlock(model: Feat594UiModel): Feat594StateBlock1 {
    return Feat594StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat594UserSummary> {
    val list = java.util.ArrayList<Feat594UserSummary>(users.size)
    for (user in users) {
        list += Feat594UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat594UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat594UiModel {
    val summaries = (0 until count).map {
        Feat594UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat594UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat594UiModel> {
    val models = java.util.ArrayList<Feat594UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat594AnalyticsEvent1(val name: String, val value: String)
data class Feat594AnalyticsEvent2(val name: String, val value: String)
data class Feat594AnalyticsEvent3(val name: String, val value: String)
data class Feat594AnalyticsEvent4(val name: String, val value: String)
data class Feat594AnalyticsEvent5(val name: String, val value: String)
data class Feat594AnalyticsEvent6(val name: String, val value: String)
data class Feat594AnalyticsEvent7(val name: String, val value: String)
data class Feat594AnalyticsEvent8(val name: String, val value: String)
data class Feat594AnalyticsEvent9(val name: String, val value: String)
data class Feat594AnalyticsEvent10(val name: String, val value: String)

fun logFeat594Event1(event: Feat594AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat594Event2(event: Feat594AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat594Event3(event: Feat594AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat594Event4(event: Feat594AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat594Event5(event: Feat594AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat594Event6(event: Feat594AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat594Event7(event: Feat594AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat594Event8(event: Feat594AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat594Event9(event: Feat594AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat594Event10(event: Feat594AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat594Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat594Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat594Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat594Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat594Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat594Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat594Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat594Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat594Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat594Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat594(u: CoreUser): Feat594Projection1 =
    Feat594Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat594Projection1> {
    val list = java.util.ArrayList<Feat594Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat594(u)
    }
    return list
}
