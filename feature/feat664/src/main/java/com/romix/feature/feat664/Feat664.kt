package com.romix.feature.feat664

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat664Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat664UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat664FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat664UserSummary
)

data class Feat664UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat664NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat664Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat664Config = Feat664Config()
) {

    fun loadSnapshot(userId: Long): Feat664NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat664NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat664UserSummary {
        return Feat664UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat664FeedItem> {
        val result = java.util.ArrayList<Feat664FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat664FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat664UiMapper {

    fun mapToUi(model: List<Feat664FeedItem>): Feat664UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat664UiModel(
            header = UiText("Feat664 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat664UiModel =
        Feat664UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat664UiModel =
        Feat664UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat664UiModel =
        Feat664UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat664Service(
    private val repository: Feat664Repository,
    private val uiMapper: Feat664UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat664UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat664UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat664UserItem1(val user: CoreUser, val label: String)
data class Feat664UserItem2(val user: CoreUser, val label: String)
data class Feat664UserItem3(val user: CoreUser, val label: String)
data class Feat664UserItem4(val user: CoreUser, val label: String)
data class Feat664UserItem5(val user: CoreUser, val label: String)
data class Feat664UserItem6(val user: CoreUser, val label: String)
data class Feat664UserItem7(val user: CoreUser, val label: String)
data class Feat664UserItem8(val user: CoreUser, val label: String)
data class Feat664UserItem9(val user: CoreUser, val label: String)
data class Feat664UserItem10(val user: CoreUser, val label: String)

data class Feat664StateBlock1(val state: Feat664UiModel, val checksum: Int)
data class Feat664StateBlock2(val state: Feat664UiModel, val checksum: Int)
data class Feat664StateBlock3(val state: Feat664UiModel, val checksum: Int)
data class Feat664StateBlock4(val state: Feat664UiModel, val checksum: Int)
data class Feat664StateBlock5(val state: Feat664UiModel, val checksum: Int)
data class Feat664StateBlock6(val state: Feat664UiModel, val checksum: Int)
data class Feat664StateBlock7(val state: Feat664UiModel, val checksum: Int)
data class Feat664StateBlock8(val state: Feat664UiModel, val checksum: Int)
data class Feat664StateBlock9(val state: Feat664UiModel, val checksum: Int)
data class Feat664StateBlock10(val state: Feat664UiModel, val checksum: Int)

fun buildFeat664UserItem(user: CoreUser, index: Int): Feat664UserItem1 {
    return Feat664UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat664StateBlock(model: Feat664UiModel): Feat664StateBlock1 {
    return Feat664StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat664UserSummary> {
    val list = java.util.ArrayList<Feat664UserSummary>(users.size)
    for (user in users) {
        list += Feat664UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat664UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat664UiModel {
    val summaries = (0 until count).map {
        Feat664UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat664UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat664UiModel> {
    val models = java.util.ArrayList<Feat664UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat664AnalyticsEvent1(val name: String, val value: String)
data class Feat664AnalyticsEvent2(val name: String, val value: String)
data class Feat664AnalyticsEvent3(val name: String, val value: String)
data class Feat664AnalyticsEvent4(val name: String, val value: String)
data class Feat664AnalyticsEvent5(val name: String, val value: String)
data class Feat664AnalyticsEvent6(val name: String, val value: String)
data class Feat664AnalyticsEvent7(val name: String, val value: String)
data class Feat664AnalyticsEvent8(val name: String, val value: String)
data class Feat664AnalyticsEvent9(val name: String, val value: String)
data class Feat664AnalyticsEvent10(val name: String, val value: String)

fun logFeat664Event1(event: Feat664AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat664Event2(event: Feat664AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat664Event3(event: Feat664AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat664Event4(event: Feat664AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat664Event5(event: Feat664AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat664Event6(event: Feat664AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat664Event7(event: Feat664AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat664Event8(event: Feat664AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat664Event9(event: Feat664AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat664Event10(event: Feat664AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat664Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat664Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat664Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat664Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat664Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat664Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat664Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat664Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat664Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat664Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat664(u: CoreUser): Feat664Projection1 =
    Feat664Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat664Projection1> {
    val list = java.util.ArrayList<Feat664Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat664(u)
    }
    return list
}
