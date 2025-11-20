package com.romix.feature.feat539

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat539Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat539UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat539FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat539UserSummary
)

data class Feat539UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat539NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat539Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat539Config = Feat539Config()
) {

    fun loadSnapshot(userId: Long): Feat539NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat539NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat539UserSummary {
        return Feat539UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat539FeedItem> {
        val result = java.util.ArrayList<Feat539FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat539FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat539UiMapper {

    fun mapToUi(model: List<Feat539FeedItem>): Feat539UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat539UiModel(
            header = UiText("Feat539 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat539UiModel =
        Feat539UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat539UiModel =
        Feat539UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat539UiModel =
        Feat539UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat539Service(
    private val repository: Feat539Repository,
    private val uiMapper: Feat539UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat539UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat539UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat539UserItem1(val user: CoreUser, val label: String)
data class Feat539UserItem2(val user: CoreUser, val label: String)
data class Feat539UserItem3(val user: CoreUser, val label: String)
data class Feat539UserItem4(val user: CoreUser, val label: String)
data class Feat539UserItem5(val user: CoreUser, val label: String)
data class Feat539UserItem6(val user: CoreUser, val label: String)
data class Feat539UserItem7(val user: CoreUser, val label: String)
data class Feat539UserItem8(val user: CoreUser, val label: String)
data class Feat539UserItem9(val user: CoreUser, val label: String)
data class Feat539UserItem10(val user: CoreUser, val label: String)

data class Feat539StateBlock1(val state: Feat539UiModel, val checksum: Int)
data class Feat539StateBlock2(val state: Feat539UiModel, val checksum: Int)
data class Feat539StateBlock3(val state: Feat539UiModel, val checksum: Int)
data class Feat539StateBlock4(val state: Feat539UiModel, val checksum: Int)
data class Feat539StateBlock5(val state: Feat539UiModel, val checksum: Int)
data class Feat539StateBlock6(val state: Feat539UiModel, val checksum: Int)
data class Feat539StateBlock7(val state: Feat539UiModel, val checksum: Int)
data class Feat539StateBlock8(val state: Feat539UiModel, val checksum: Int)
data class Feat539StateBlock9(val state: Feat539UiModel, val checksum: Int)
data class Feat539StateBlock10(val state: Feat539UiModel, val checksum: Int)

fun buildFeat539UserItem(user: CoreUser, index: Int): Feat539UserItem1 {
    return Feat539UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat539StateBlock(model: Feat539UiModel): Feat539StateBlock1 {
    return Feat539StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat539UserSummary> {
    val list = java.util.ArrayList<Feat539UserSummary>(users.size)
    for (user in users) {
        list += Feat539UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat539UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat539UiModel {
    val summaries = (0 until count).map {
        Feat539UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat539UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat539UiModel> {
    val models = java.util.ArrayList<Feat539UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat539AnalyticsEvent1(val name: String, val value: String)
data class Feat539AnalyticsEvent2(val name: String, val value: String)
data class Feat539AnalyticsEvent3(val name: String, val value: String)
data class Feat539AnalyticsEvent4(val name: String, val value: String)
data class Feat539AnalyticsEvent5(val name: String, val value: String)
data class Feat539AnalyticsEvent6(val name: String, val value: String)
data class Feat539AnalyticsEvent7(val name: String, val value: String)
data class Feat539AnalyticsEvent8(val name: String, val value: String)
data class Feat539AnalyticsEvent9(val name: String, val value: String)
data class Feat539AnalyticsEvent10(val name: String, val value: String)

fun logFeat539Event1(event: Feat539AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat539Event2(event: Feat539AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat539Event3(event: Feat539AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat539Event4(event: Feat539AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat539Event5(event: Feat539AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat539Event6(event: Feat539AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat539Event7(event: Feat539AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat539Event8(event: Feat539AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat539Event9(event: Feat539AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat539Event10(event: Feat539AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat539Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat539Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat539Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat539Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat539Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat539Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat539Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat539Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat539Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat539Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat539(u: CoreUser): Feat539Projection1 =
    Feat539Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat539Projection1> {
    val list = java.util.ArrayList<Feat539Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat539(u)
    }
    return list
}
